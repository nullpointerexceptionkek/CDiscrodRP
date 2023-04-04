/*
 *
 * MIT License
 *
 * Copyright (c) 2023 lee
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package lee.aspect.dev.cdiscordrp.util.system;

import lee.aspect.dev.cdiscordrp.exceptions.FileNotAJarException;
import lee.aspect.dev.cdiscordrp.exceptions.UnsupportedOSException;

import java.io.*;
import java.net.URISyntaxException;


public class StartLaunch {

    private static final File STARTUPDIR_WINDOWS = new File(System.getProperty("user.home") + "\\AppData\\Roaming\\Microsoft\\Windows\\Start Menu\\Programs\\Startup");
    private static final File STARTUPDIR_LINUX = new File("/etc/init.d/");
    private static final File STARTUPDIR_MAC = new File(System.getProperty("user.home") + "/Library/LaunchAgents/");

    private static final String APP_NAME = "CDiscordRP";
    private static final String APP_SCRIPT_WINDOWS = "CDRP.bat";
    private static final String APP_SCRIPT_LINUX = APP_NAME;
    private static final String APP_SCRIPT_MAC = APP_NAME + ".plist";

    public static void createStartupScript() throws IOException, UnsupportedOSException, FileNotAJarException, URISyntaxException {
        final File currentJar = new File(RestartApplication.class.getProtectionDomain().getCodeSource().getLocation().toURI());

        if (!currentJar.getName().endsWith(".jar")) {
            throw new FileNotAJarException();
        }

        if (isOnWindows()) {
            File batFile = new File(STARTUPDIR_WINDOWS, APP_SCRIPT_WINDOWS);
            PrintWriter writer = new PrintWriter(new FileWriter(batFile));
            writer.println("start \"\" javaw -jar " + currentJar + " --StartLaunch");
            writer.close();
        } else if (isOnLinux()) {
            File scriptFile = new File(STARTUPDIR_LINUX, APP_SCRIPT_LINUX);
            PrintWriter writer = new PrintWriter(new FileWriter(scriptFile));
            writer.println("#!/bin/sh");
            writer.println("java -jar " + currentJar);
            writer.close();
            boolean created = scriptFile.setExecutable(true);
            if (!created) {
                throw new IOException("Could not set executable permission on " + scriptFile);
            }

            ProcessBuilder pb = new ProcessBuilder("/usr/sbin/insserv", "-d", "/etc/init.d/" + APP_NAME);
            pb.redirectErrorStream(true);
            Process p = pb.start();
            try {
                p.waitFor();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            if (p.exitValue() != 0) {
                throw new IOException("Could not add " + scriptFile + " to startup scripts: " + readStream(p.getInputStream()));
            }

        } else if (isOnMac()) {
            File plistFile = new File(STARTUPDIR_MAC, APP_SCRIPT_MAC);
            PrintWriter writer = new PrintWriter(new FileWriter(plistFile));
            writer.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            writer.println("<!DOCTYPE plist PUBLIC \"-//Apple//DTD PLIST 1.0//EN\" \"http://www.apple.com/DTDs/PropertyList-1.0.dtd\">");
            writer.println("<plist version=\"1.0\">");
            writer.println("<dict>");
            writer.println("    <key>Label</key>");
            writer.println("    <string>" + APP_NAME + "</string>");
            writer.println("    <key>ProgramArguments</key>");
            writer.println("    <array>");
            writer.println("        <string>java</string>");
            writer.println("        <string>-jar</string>");
            writer.println("        <string>" + currentJar + "</string>");
            writer.println("    </array>");
            writer.println("    <key>RunAtLoad</key>");
            writer.println("    <true/>");
            writer.println("</dict>");
            writer.println("</plist>");
            writer.close();
        } else {
            throw new UnsupportedOSException("Start Launch currently only supports Windows, Linux, and macOS");
        }

    }

    private static String readStream(InputStream stream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
            sb.append(System.lineSeparator());
        }
        return sb.toString();
    }

    public static boolean isOnWindows() {
        String osName = System.getProperty("os.name").toLowerCase();
        return osName.contains("win");
    }
    public static boolean isOnLinux() {
        String osName = System.getProperty("os.name").toLowerCase();
        return osName.contains("nix") || osName.contains("nux") || osName.contains("aix");
    }

    public static boolean isOnMac() {
        String osName = System.getProperty("os.name").toLowerCase();
        return osName.contains("mac");
    }

    public static boolean isBatCreated() {
        return isOnWindows() && new File(STARTUPDIR_WINDOWS, APP_SCRIPT_WINDOWS).exists();
    }

    public static boolean isStartupScriptCreated() {
        if (isOnWindows()) {
            return new File(STARTUPDIR_WINDOWS, APP_SCRIPT_WINDOWS).exists();
        } else if (isOnLinux()) {
            return new File(STARTUPDIR_LINUX, APP_SCRIPT_LINUX).exists();
        } else if (isOnMac()) {
            return new File(STARTUPDIR_MAC, APP_SCRIPT_MAC).exists();
        } else {
            return false;
        }
    }

    public static void deleteStartupScript() {
        if (isOnWindows()) {
            File batFile = new File(STARTUPDIR_WINDOWS, APP_SCRIPT_WINDOWS);
            if (batFile.exists()) {
                batFile.delete();
            }
        } else if (isOnLinux()) {
            File scriptFile = new File(STARTUPDIR_LINUX, APP_SCRIPT_LINUX);
            if (scriptFile.exists()) {
                scriptFile.delete();
            }
            try {
                Runtime.getRuntime().exec("update-rc.d -f " + APP_NAME + " remove");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else if (isOnMac()) {
            File plistFile = new File(STARTUPDIR_MAC, APP_SCRIPT_MAC);
            if (plistFile.exists()) {
                plistFile.delete();
            }
        }
    }
}


