package com.example.demo;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;

@Component
public class H2ConsoleDarkModeFilter extends OncePerRequestFilter {

    private static final String DARK_MODE_CSS = """
            <style>
            body { background-color: #1e1e1e !important; color: #d4d4d4 !important; }
            a { color: #569cd6 !important; }
            a:hover { color: #9cdcfe !important; }
            h1, h2, h3 { color: #569cd6 !important; }
            table { background-color: #252526 !important; border-color: #3c3c3c !important; }
            td, th { background-color: #252526 !important; color: #d4d4d4 !important; border-color: #3c3c3c !important; }
            th { color: #9cdcfe !important; }
            tr:hover td { background-color: #2d2d30 !important; }
            input[type=text], input[type=password], select, textarea {
                background-color: #3c3c3c !important;
                color: #d4d4d4 !important;
                border: 1px solid #555 !important;
            }
            button, input[type=submit], input[type=button] {
                background-color: #0e639c !important;
                color: #ffffff !important;
                border: 1px solid #1177bb !important;
            }
            button:hover, input[type=submit]:hover, input[type=button]:hover {
                background-color: #1177bb !important;
            }
            div.toolbar { background-color: #333333 !important; border-color: #3c3c3c !important; }
            .error { background-color: #5a1d1d !important; color: #f48771 !important; }
            form.login { background-color: #252526 !important; border: 1px solid #3c3c3c !important; }
            </style>
            """;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        if (!request.getRequestURI().startsWith("/h2-console")) {
            chain.doFilter(request, response);
            return;
        }

        ContentCachingResponseWrapper wrapper = new ContentCachingResponseWrapper(response);
        chain.doFilter(request, wrapper);

        String contentType = wrapper.getContentType();
        if (contentType != null && contentType.contains("text/html")) {
            byte[] content = wrapper.getContentAsByteArray();
            String encoding = wrapper.getCharacterEncoding() != null ? wrapper.getCharacterEncoding() : "UTF-8";
            String html = new String(content, encoding);

            if (html.contains("</head>")) {
                html = html.replace("</head>", DARK_MODE_CSS + "</head>");
            }

            byte[] modified = html.getBytes(encoding);
            response.setContentLength(modified.length);
            response.getOutputStream().write(modified);
        } else {
            wrapper.copyBodyToResponse();
        }
    }
}
