package netty;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class RouterHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) {
        String uri = request.uri();
        String responseText;

        // Parse query parameters
        QueryStringDecoder decoder = new QueryStringDecoder(uri);
        String path = decoder.path();
        Map<String, List<String>> params = decoder.parameters();

        switch (path) {
            case "/hello":
                responseText = "Hello from Netty!";
                break;

            case "/time":
                responseText = "Server time: " + LocalDateTime.now();
                break;

            case "/echo":
                String msg = params.getOrDefault("msg", List.of("")).stream().findFirst().orElse("");
                responseText = "Echo: " + msg;
                break;

            default:
                responseText = "404 Not Found";
                sendResponse(ctx, responseText, HttpResponseStatus.NOT_FOUND);
                return;
        }

        sendResponse(ctx, responseText, HttpResponseStatus.OK);
    }

    private void sendResponse(ChannelHandlerContext ctx, String responseText, HttpResponseStatus status) {
        byte[] bytes = responseText.getBytes(StandardCharsets.UTF_8);
        FullHttpResponse response = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1,
                status,
                Unpooled.wrappedBuffer(bytes)
        );
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain; charset=UTF-8");
        response.headers().set(HttpHeaderNames.CONTENT_LENGTH, bytes.length);
        ctx.writeAndFlush(response);
    }
}
