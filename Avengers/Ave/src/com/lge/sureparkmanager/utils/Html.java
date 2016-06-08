package com.lge.sureparkmanager.utils;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

public class Html {
    public static String getHtmlHeader() {
        String htmlHeader = "<!DOCTYPE html>";
        htmlHeader += "<head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=EUC-KR\">";
        htmlHeader += "<title>Sure-Park</title>";
        htmlHeader += "<link rel=\"stylesheet\" href=\"style.css\" type=\"text/css\"></head>";
        return htmlHeader;
    }

    public static String getHtmlFooter() {
        String htmlFooter = "</html>";
        return htmlFooter;
    }

    public static void writeHTML(HttpServletResponse response, String message) {
        response.setContentType("text/html; charset=euc-kr");

        PrintWriter out = null;
        try {
            out = response.getWriter();
            out.write(getHtmlHeader());

            out.write("<body>");
            out.write(message);
            out.write("</body>");

            out.write(getHtmlFooter());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }
}
