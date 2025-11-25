package netty;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.*;

public class NettyHttpServer {

    public static void main(String[] args) throws InterruptedException {
        // Accepts connections (usually 1 thread)
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        // Handles requests (by default, number of CPU cores)
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            ChannelPipeline p = ch.pipeline();
                            p.addLast(new HttpServerCodec());            // HTTP encode/decode
                            p.addLast(new HttpObjectAggregator(65536));  // Full HTTP message
                            p.addLast(new RouterHandler() {
                                @Override
                                protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg) {
                                    byte[] content = "Hello from Netty\n".getBytes();

                                    FullHttpResponse response = new DefaultFullHttpResponse(
                                            HttpVersion.HTTP_1_1,
                                            HttpResponseStatus.OK,
                                            Unpooled.wrappedBuffer(content)
                                    );
                                    response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain");
                                    response.headers().set(HttpHeaderNames.CONTENT_LENGTH, content.length);

                                    ctx.writeAndFlush(response);
                                }
                            });
                        }
                    });

            ChannelFuture f = b.bind(8000).sync();
            System.out.println("Netty server started on port 8000");
            f.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
