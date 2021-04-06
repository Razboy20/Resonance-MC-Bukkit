package net.thiccaxe.resonance.util;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;

import java.util.Optional;

public class HttpUtility {
    private static final char[] res = new char[]{'H', 'e', 'l', 'o', ' ', 'w', 'o', 'r', 'l', 'd'};


    private static FullHttpRequest req;

    public static boolean upgrade(ChannelHandlerContext ctx, FullHttpRequest request) {
        if (request.headers().get("Connection") == null || request.headers().get("Upgrade") == null) {
            return false;
        }
        if (request.headers().get("Connection").equalsIgnoreCase("upgrade") && request.headers().get("Upgrade").equalsIgnoreCase("websocket")) {
            WebSocketServerHandshakerFactory factory = new WebSocketServerHandshakerFactory("ws://thiccaxe.net/", null, true);
            WebSocketServerHandshaker handshaker = factory.newHandshaker(request);
            if (handshaker == null) {
                WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
                return false;
            }
            handshaker.handshake(ctx.channel(), request);

            return true;

        }
        return false;
    }

    public static String getRemoteAddress(ChannelHandlerContext ctx, FullHttpRequest request) {
        HttpHeaders headers = request.headers();
        String ip = headers.get("X-Forwarded-For");
        if (ip != null) {
            return ip;
        }
        return ctx.channel().remoteAddress().toString();
    }

    public static String getMimeType(String extension) {
        switch (extension) {
            case ".aac":
                return "audio/aac";
            case ".abw":
                return "application/x-abiword";
            case ".arc":
                return "application/x-freearc";
            case ".avi":
                return "video/x-msvideo";
            case ".azw":
                return "application/vnd.amazon.ebook";
            case ".bin":
                return "application/octet-stream";
            case ".bmp":
                return "image/bmp";
            case ".bz":
                return "application/x-bzip";
            case ".bz2":
                return "application/x-bzip2";
            case ".csh":
                return "application/x-csh";
            case ".css":
                return "text/css";
            case ".csv":
                return "text/csv";
            case ".doc":
                return "application/msword";
            case ".docx":
                return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            case ".eot":
                return "application/vnd.ms-fontobject";
            case ".epub":
                return "application/epub+zip";
            case ".gz":
                return "application/gzip";
            case ".gif":
                return "image/gif";
            case ".htm":
            case ".html":
                return "text/html";
            case ".ico":
                return "image/vnd.microsoft.icon";
            case ".ics":
                return "text/calendar";
            case ".jar":
                return "application/java-archive";
            case ".jpg":
            case ".jpeg":
                return "image/jpeg";
            case ".js":
                return "text/javascript";
            case ".json":
                return "application/json";
            case ".jsonld":
                return "application/ld+json";
            case ".mid":
                return "audio/midi";
            case ".mjs":
                return "text/javascript";
            case ".mp3":
                return "audio/mpeg";
            case ".cda":
                return "application/x-cdf";
            case ".mp4":
                return "video/mp4";
            case ".mpeg":
                return "video/mpeg";
            case ".mpkg":
                return "application/vnd.apple.installer+xml";
            case ".odp":
                return "application/vnd.oasis.opendocument.presentation";
            case ".ods":
                return "application/vnd.oasis.opendocument.spreadsheet";
            case ".odt":
                return "application/vnd.oasis.opendocument.text";
            case ".oga":
                return "audio/ogg";
            case ".ogv":
                return "video/ogg";
            case ".ogx":
                return "application/ogg";
            case ".opus":
                return "audio/opus";
            case ".otf":
                return "font/otf";
            case ".png":
                return "image/png";
            case ".pdf":
                return "application/pdf";
            case ".php":
                return "application/x-httpd-php";
            case ".ppt":
                return "application/vnd.ms-powerpoint";
            case ".pptx":
                return "application/vnd.openxmlformats-officedocument.presentationml.presentation";
            case ".rar":
                return "application/vnd.rar";
            case ".rtf":
                return "application/rtf";
            case ".sh":
                return "application/x-sh";
            case ".svg":
                return "image/svg+xml";
            case ".swf":
                return "application/x-shockwave-flash";
            case ".tar":
                return "application/x-tar";
            case ".tif":
                return "image/tiff";
            case ".ts":
                return "video/mp2t";
            case ".ttf":
                return "font/ttf";
            default:
            case ".txt":
                return "text/plain";
            case ".vsd":
                return "application/vnd.visio";
            case ".wav":
                return "audio/wav";
            case ".weba":
                return "audio/webm";
            case ".webm":
                return "video/webm";
            case ".webp":
                return "image/webp";
            case ".woff":
                return "font/woff";
            case ".woff2":
                return "font/woff2";
            case ".xhtml":
                return "application/xhtml+xml";
            case ".xls":
                return "application/vnd.ms-excel";
            case ".xlsx":
                return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            case ".xml":
                return "application/xml ";
            case ".xul":
                return "application/vnd.mozilla.xul+xml";
            case ".zip":
                return "application/zip";
            case ".3gp":
                return "video/3gpp";
            case ".3g2":
                return "video/3gpp2";
            case ".7z":
                return "application/x-7z-compressed";
        }
    }
    public static Optional<String> getExtension(String filename) {
        return Optional.ofNullable(filename)
                .filter(f -> f.contains("."))
                .map(f -> f.substring(filename.lastIndexOf(".") + 1));
    }

}