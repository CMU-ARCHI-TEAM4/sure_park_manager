package com.lge.sureparkmanager.ui;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Base64;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.lge.sureparkmanager.manager.DataBaseManager;
import com.lge.sureparkmanager.manager.LogManager;
import com.lge.sureparkmanager.manager.SystemManager;
import com.lge.sureparkmanager.utils.Html;
import com.lge.sureparkmanager.utils.Log;
import com.lge.sureparkmanager.utils.WebSession;

import nl.captcha.Captcha;
import nl.captcha.backgrounds.GradiatedBackgroundProducer;

@WebServlet(description = "login", urlPatterns = { "/ad_login", "/login" })
public class Login extends HttpServlet {
    private static final String TAG = Login.class.getSimpleName();
    private static final long serialVersionUID = 1L;
    private static final int MAX_FAIL = 3;

    private LogManager lm = (LogManager) SystemManager.getInstance()
            .getManager(SystemManager.LOG_MANAGER);

    public Login() {
        super();
        Log.d(TAG, "init");
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        final StringBuffer requestUrl = request.getRequestURL();

        request.setCharacterEncoding("euc-kr");
        DataBaseManager dbm = (DataBaseManager) SystemManager.getInstance()
                .getManager(SystemManager.DATABASE_MANAGER);

        String id = request.getParameter("id");
        String pw = request.getParameter("passwd");
        String captchaUser = request.getParameter("captcha");
        String otp = request.getParameter("otp");

        HttpSession session = request.getSession();

        String captchaAnswer = (String) session.getAttribute(WebSession.CAPTCH_ANSWER);
        captchaAnswer = captchaAnswer == null ? "" : captchaAnswer;

        int optAnswer = -1;
        if (session.getAttribute(WebSession.OPT_ANSWER) != null) {
            optAnswer = ((Integer) session.getAttribute(WebSession.OPT_ANSWER)).intValue();
        }

        if (captchaAnswer.equals(captchaUser)) {
            Log.d(TAG, "Equals Confirmed with captcha " + captchaAnswer);
        }

        Log.d(TAG, "OTP  " + otp + "  ANSWER " + optAnswer);

        if (dbm != null && dbm.getQueryWrapper().isLoginOk(id, pw)
                && captchaAnswer.equals(captchaUser)
                && (optAnswer == -1 || (otp != null && Integer.parseInt(otp) == optAnswer))) {

            Log.d(TAG, "Athorized user " + captchaAnswer);

            session.setAttribute(WebSession.SESSION_USERID, id);
            session.removeAttribute(WebSession.SESSION_LOGIN_FAILED_COUNT);
            session.removeAttribute(WebSession.OPT_ANSWER);

            if ("attendant".equals(id)) {
                lm.log(LogManager.ATTENDANT, LogManager.LOGIN);
                response.sendRedirect("pf");
            } else if ("admin".equals(id)) {
                lm.log(LogManager.OWNER, LogManager.LOGIN);
                response.sendRedirect("statistics");
            } else {
                lm.log(LogManager.DRIVER, LogManager.LOGIN + "/" + id);
                session.setMaxInactiveInterval(WebSession.SESSION_TIMEOUT);
                response.sendRedirect("reservation");
            }
        } else {

            PrintWriter printWriter = null;
            try {
                printWriter = response.getWriter();
                printWriter.write(Html.getHtmlHeader());

                printWriter.write(getJsInputCheckSubmit("action_login"));

                if (requestUrl.toString().contains("ad_login")) {

                    int failCount = 0;
                    if (session.getAttribute(WebSession.SESSION_LOGIN_FAILED_COUNT) != null) {
                        failCount = ((Integer) session
                                .getAttribute(WebSession.SESSION_LOGIN_FAILED_COUNT)).intValue();
                    }
                    ;

                    failCount++;
                    if (failCount > MAX_FAIL) {
                        // notify through ssm
                        lm.log(LogManager.OWNER,
                                LogManager.UNATHORIZED_LOGIN_DETECTED + " / " + id.toUpperCase());
                        printWriter.write(getJsAlert());
                        session.removeAttribute(WebSession.SESSION_LOGIN_FAILED_COUNT);
                    } else {
                        session.setAttribute(WebSession.SESSION_LOGIN_FAILED_COUNT, failCount);
                    }

                    printWriter.write(getLoginHtml("Login for Administrator", session));

                    // make OPT token
                    Random rand = new Random();
                    optAnswer = rand.nextInt((Integer.MAX_VALUE - 10000) + 1) + 10000;
                    printWriter.write(getJsPrompt(optAnswer));

                    session.setAttribute(WebSession.OPT_ANSWER, optAnswer);

                } else {
                    optAnswer = -1; // initialize token
                    printWriter.write(getLoginHtml("Login for Driver", session));
                }

                printWriter.write(Html.getHtmlFooter());
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (printWriter != null) {
                    printWriter.close();
                }
            }
        }

    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

    private String makeCaptcha(HttpSession session) {

        Captcha captcha = new Captcha.Builder(120, 50).addText()
                .addBackground(new GradiatedBackgroundProducer()).addNoise()
                // .gimp(new DropShadowGimpyRenderer())
                .addBorder().build();

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            ImageIO.write(captcha.getImage(), "PNG", out);
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] bytes = out.toByteArray();

        byte[] base64bytes = Base64.getEncoder().encode(bytes);
        String src = "data:image/png;base64," + new String(base64bytes);

        session.setAttribute(WebSession.CAPTCH_ANSWER, captcha.getAnswer());
        return src;
    }

    private String getLoginHtml(String title, HttpSession session) {

        final String captcha = makeCaptcha(session);
        String html = "";
        String action = "";
        String image = "";

        if (title.contains("Administrator")) {
            action = "<form action='ad_login' name='action_login' method='post'>";
            image = "<img src='images/lattanze.jpg' />";
        } else {
            action = "<form action='login' name='action_login' method='post'>";
            image = "<img src='images/hulk_icon.png' width='400' height='200' />";
        }

        html += action;
        html += "<table class='centerTable' width='500'>";
        html += "<tr ><td colspan='2' align='center'>";
        html += image;
        html += "</td></tr><tr><td align='center'>ID</td>";
        html += "<td><input type='text' name='id' id='id' size='20' /></td>";
        html += "</tr><tr><td align='center'>PW</td>";
        html += "<td><input type='password' name='passwd' id='passwd' size='20' /></td></tr>";
        if (title.contains("Administrator")) {
            html += "</tr><tr><td align='center'>OTP TOKEN</td>";
            html += "<td><input type='text' name='otp' id='otp' size='20' /></td>";
        }
        html += "<tr><td><img alt='lg twins' src='" + captcha + "' /></td>";
        html += "<td><input type='text' name='captcha' id='captcha' /></tr>";

        html += "<tr ><td colspan='2' align='center'>";
        html += "<a href='javascript:submitform();'><input type='button' value='Login'/></a>";
        html += "<a href='javascript:window.history.back();'><input type='button' value='Cancle'/></a>";
        html += "</td></tr></table></form>";

        return html;
    }

    private String getJsInputCheckSubmit(String formName) {
        String html = "<script type='text/javascript'>function submitform() {";
        html += "if(document." + formName + ".onsubmit && !document." + formName + ".onsubmit()) {";
        html += "return; }";
        html += "if(document.getElementById('id').value == '') { alert('Please input id.'); return; }";
        html += "if(document.getElementById('passwd').value == '') { alert('Please input password.'); return; }";
        html += "document." + formName + ".submit(); } </script>";
        return html;
    }

    private String getJsAlert() {
        String html = "<script type='text/javascript'> alert('THIS IS SMS. UNATHORIZE USER DETECTED !!!.'); </script>";
        return html;
    }

    private String getJsPrompt(int token) {
        String html = "<script type='text/javascript'> prompt('THIS IS SMS FROM SYSTEM. YOUR TOKEN IS', '"
                + token + "'); </script>";
        return html;
    }
}
