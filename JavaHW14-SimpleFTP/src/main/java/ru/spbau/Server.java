package ru.spbau;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


public class Server {

    public final static int PORT = 1234;

    private final Path path;

    private volatile boolean isWorking;

    private Thread thread;

    private volatile Selector selector;

    private static Logger logger = LoggerFactory.getLogger(Server.class);


    public Server(Path path) {
        this.path = path;
    }

    public void start() throws IOException {

        logger.debug("start");

        isWorking = true;

        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();

        serverSocketChannel.bind(new InetSocketAddress(PORT));

        serverSocketChannel.configureBlocking(false);

        selector = Selector.open();

        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        thread = new Thread(() -> {

            try {

                while(isWorking){

                    selector.select();

                    Set<SelectionKey> selectedKeys = selector.selectedKeys();

                    Iterator<SelectionKey> keyIterator = selectedKeys.iterator();

                    while(keyIterator.hasNext()) {

                        SelectionKey key = keyIterator.next();

                        if(key.isAcceptable()) {

                            logger.debug("in acceptable");

                            SocketChannel socketChannel =
                                    serverSocketChannel.accept();

                            socketChannel.configureBlocking(false);

                            socketChannel.register(selector, SelectionKey.OP_READ);

                        } else if (key.isReadable()) {

                            logger.debug("in readable");

                            SocketChannel channel = (SocketChannel)key.channel();
                            MessageRead message = (MessageRead)key.attachment();

                            MessageStructure messageStructure = new MessageStructure(channel);

                            ByteBuffer typeBuf = null;
                            ByteBuffer sizeBuf = null;
                            ByteBuffer pathBuf = null;

                            if (message != null) {
                                typeBuf = message.getType();
                                sizeBuf = message.getSize();
                                pathBuf = message.getBuffer();
                            }

                            if (typeBuf == null) {
                                typeBuf = messageStructure.readIntToBuffer();
                            }

                            channel.read(typeBuf);

                            if (!typeBuf.hasRemaining()) {

                                typeBuf.flip();

                                if (sizeBuf == null) {
                                    sizeBuf = messageStructure.readIntToBuffer();
                                }

                                channel.read(sizeBuf);

                                if (!sizeBuf.hasRemaining()) {

                                    sizeBuf.flip();

                                    if (pathBuf == null) {
                                        pathBuf = messageStructure.readBytesToBuffer(sizeBuf.getInt());
                                    }

                                    channel.read(pathBuf);

                                    if (!pathBuf.hasRemaining()) {

                                        pathBuf.flip();

                                        Path requestedPath = path.resolve(new String(pathBuf.array()));

                                        SelectionKey selectionKey = channel.register(selector, SelectionKey.OP_WRITE);
                                        selectionKey.attach(new MessageWithRequest(typeBuf.getInt(), requestedPath));

                                        continue;
                                    }

                                }

                            }

                            SelectionKey selectionKey = channel.register(selector, SelectionKey.OP_READ);
                            selectionKey.attach(new MessageRead(typeBuf, sizeBuf, pathBuf));

                        } else if (key.isWritable()) {

                            logger.debug("in writable");

                            AttachedMessage message = (AttachedMessage)key.attachment();
                            SocketChannel channel = (SocketChannel)key.channel();

                            ByteBuffer buffer;

                            if (message.getMessageType() == AttachedMessage.REQUEST) {

                                logger.debug("new request");

                                MessageWithRequest request = (MessageWithRequest)message;

                                if (request.getType() == MessageStructure.listType) {
                                    buffer = list(request.getPath());
                                } else {
                                    buffer = get(request.getPath());
                                }

                            } else {

                                logger.debug("continue writing");

                                buffer = ((MessageWrite)message).getBuffer();

                            }

                            logger.debug("try to send {} bytes", buffer.remaining());
                            channel.write(buffer);
                            logger.debug("{} bytes left", buffer.remaining());

                            key.attach(new MessageWrite(buffer));

                        }

                        keyIterator.remove();
                    }

                }

                selector.close();
                serverSocketChannel.close();

            } catch (IOException e) {

                throw new RuntimeException(e);

            }

        });

        thread.start();

    }

    private ByteBuffer list(Path requestedPath) throws IOException {

        logger.debug("list with path {}", requestedPath);

        ByteBuffer buffer = ByteBuffer.allocate(100);

        BufferMessageStructure messageStructure = new BufferMessageStructure(buffer);

        if (!Files.exists(requestedPath)) {

            messageStructure.writeInt(0);

        } else {

            List<Path> pathList = Files.list(requestedPath).collect(Collectors.toList());

            messageStructure.writeInt(pathList.size());

            for (Path path : pathList) {
                messageStructure.writeString(path.getFileName().toString());
                messageStructure.writeBool(Files.isDirectory(path));
            }

        }

        buffer.flip();

        return buffer;

    }

    private ByteBuffer get(Path requestedPath) throws IOException {

        logger.debug("get with path {}", requestedPath);

        ByteBuffer buffer = ByteBuffer.allocate(100);

        BufferMessageStructure messageStructure = new BufferMessageStructure(buffer);

        if (!Files.exists(requestedPath) || !Files.isRegularFile(requestedPath)) {
            messageStructure.writeInt(0);
        }

        messageStructure.writeByteArray(Files.readAllBytes(requestedPath));

        buffer.flip();

        return buffer;

    }

    public void stop() throws InterruptedException, IOException {

        logger.debug("stop");

        isWorking = false;
        selector.wakeup();
        thread.join();

    }

}
