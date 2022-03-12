package org.lzx.web.gateway.infrastructure.util;

import com.jcraft.jsch.*;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Vector;

/**
 * SFTP工具类
 *
 * @author LZx
 * @since 2020/12/31
 */
public class SftpUtils {

    private static final SftpLogger SFTP_LOGGER = new SftpLogger(SftpLogger.LogLevel.ERROR);

    static {
        JSch.setLogger(SFTP_LOGGER);
    }

    private SftpUtils() {
    }

    /**
     * 设置日志级别
     *
     * @param level 日志级别
     */
    public static void logLevel(SftpLogger.LogLevel level) {
        if (!SFTP_LOGGER.level.equals(level)) {
            SFTP_LOGGER.setLevel(level);
        }
    }

    /**
     * 创建会话，<strong style="color:red;">不再使用后必须关闭</strong>
     *
     * @param server 连接用户
     * @return 连接会话
     * @throws JSchException JSch异常
     */
    public static Session session(Server server) throws JSchException {
        JSch jsch = new JSch();
        Session session = jsch.getSession(server.username, server.host, server.port);
        session.setPassword(server.password);
        session.setConfig("StrictHostKeyChecking", "no");
        return session;
    }

    /**
     * 上传文件到远程服务器
     *
     * @param session                 会话
     * @param inputStream             待上传文件的流
     * @param remoteFile              上传文件绝对路径
     * @param connectionTimeoutMillis 连接超时时间
     */
    public static void uploadFile(Session session, InputStream inputStream, String remoteFile, int connectionTimeoutMillis) throws JSchException, SftpException {
        ChannelSftp channelSftp = channelSftp(session, connectionTimeoutMillis);
        channelSftp.setFilenameEncoding("UTF-8");
        try {
            channelSftp.put(inputStream, remoteFile);
        } finally {
            channelSftp.disconnect();
        }
    }

    /**
     * 上传文件到远程服务器
     *
     * @param session                 会话
     * @param targetFile              待上传文件
     * @param remoteDir               上传目的地文件夹，可以是绝对路径也可以是相对路径，可以.开头，不能以~开头
     * @param connectionTimeoutMillis 连接超时时间
     */
    public static void uploadFile(Session session, String targetFile, String remoteDir, int connectionTimeoutMillis) throws JSchException, SftpException {
        uploadFile(session, targetFile, remoteDir, false, connectionTimeoutMillis);
    }

    /**
     * 上传文件到远程服务器，由于可以多个服务/多个线程同时cd/mkdir可能会mkdir失败而报错，如不能接受该异常，则分布式锁，
     * 所以尽量使用{@link this#uploadFile(Session, String, String, int)}
     *
     * @param session                 会话
     * @param targetFile              待上传文件
     * @param remoteDir               上传目的地文件夹，可以是绝对路径也可以是相对路径，可以.开头，不能以~开头
     * @param mkdir                   当文件夹不存在时是否创建文件夹
     * @param connectionTimeoutMillis 连接超时时间
     */
    public static void uploadFile(Session session, String targetFile, String remoteDir, boolean mkdir, int connectionTimeoutMillis) throws JSchException, SftpException {
        ChannelSftp channelSftp = channelSftp(session, connectionTimeoutMillis);
        channelSftp.setFilenameEncoding("UTF-8");
        try {
            if (mkdir) {
                cdOrMkdirThenCd(channelSftp, remoteDir);
                channelSftp.put(targetFile, "./");
            } else {
                channelSftp.put(targetFile, remoteDir);
            }
        } finally {
            channelSftp.disconnect();
        }
    }

    /**
     * @param session                 会话
     * @param remoteDir               远程目标文件夹
     * @param connectionTimeoutMillis 连接超时时间
     * @return 所有LS出来实体的集合
     * @throws JSchException JSch异常
     * @throws SftpException SFTP来源的异常
     */
    @SuppressWarnings("unchecked")
    public static Vector<ChannelSftp.LsEntry> ls(Session session, final String remoteDir, int connectionTimeoutMillis) throws JSchException, SftpException {
        ChannelSftp channelSftp = channelSftp(session, connectionTimeoutMillis);
        channelSftp.setFilenameEncoding("UTF-8");
        try {
            return channelSftp.ls(remoteDir);
        } finally {
            channelSftp.disconnect();
        }
    }

    /**
     * 执行命令
     * <pre style="color:green;">
     * Available commands:           <strong style="color:red;">* means unimplemented command.</strong><br>
     * cd path                       Change remote directory to 'path'<br>
     * lcd path                      Change local directory to 'path'<br>
     * chgrp grp path                Change group of file 'path' to 'grp'<br>
     * chmod mode path               Change permissions of file 'path' to 'mode'<br>
     * chown own path                Change owner of file 'path' to 'own'<br>
     * df [path]                     Display statistics for current directory or filesystem containing 'path'<br>
     * get remote-path [local-path]  Download file<br>
     * get-resume remote-path [local-path]  Resume to download file.<br>
     * get-append remote-path [local-path]  Append remote file to local file<br>
     * hardlink oldpath newpath      Hardlink remote file<br>
     * *lls [ls-options [path]]      Display local directory listing<br>
     * ln oldpath newpath            Symlink remote file<br>
     * *lmkdir path                  Create local directory<br>
     * lpwd                          Print local working directory<br>
     * ls [path]                     Display remote directory listing<br>
     * *lumask umask                 Set local umask to 'umask'<br>
     * mkdir path                    Create remote directory<br>
     * put local-path [remote-path]  Upload file<br>
     * put-resume local-path [remote-path]  Resume to upload file<br>
     * put-append local-path [remote-path]  Append local file to remote file.<br>
     * pwd                           Display remote working directory<br>
     * stat path                     Display info about path<br>
     * exit                          Quit sftp<br>
     * quit                          Quit sftp<br>
     * rename oldpath newpath        Rename remote file<br>
     * rmdir path                    Remove remote directory<br>
     * rm path                       Delete remote file<br>
     * symlink oldpath newpath       Symlink remote file<br>
     * readlink path                 Check the target of a symbolic link<br>
     * realpath path                 Canonicalize the path<br>
     * rekey                         Key re-exchanging<br>
     * compression level             Packet compression will be enabled<br>
     * version                       Show SFTP version<br>
     * </pre>
     *
     * @param session                 会话
     * @param command                 命令
     * @param successOut              成功的输出流
     * @param errorOut                异常的输出流
     * @param connectionTimeoutMillis 连接超时时间
     * @return 0成功 1异常
     * @throws JSchException        JSch异常
     * @throws IOException          读取执行命令时返回数据时出现的异常
     * @throws InterruptedException 持续读取远端数据时，sleep线程时抛出的异常
     */
    public static int execCommand(Session session, String command, OutputStream successOut, OutputStream errorOut, int connectionTimeoutMillis) throws JSchException, InterruptedException, IOException {
        if (!session.isConnected()) {
            session.connect(connectionTimeoutMillis);
        }
        ChannelExec channelExec = channelExec(session, command, errorOut, connectionTimeoutMillis);
        try (InputStream is = channelExec.getInputStream()) {
            byte[] buf = new byte[512];
            while (true) {
                while (is.available() > 0) {
                    int len = is.read(buf);
                    if (len < 0) {
                        break;
                    } else {
                        successOut.write(buf, 0, len);
                    }
                }
                if (channelExec.isClosed()) {
                    if (is.available() > 0) {
                        continue;
                    }
                    return channelExec.getExitStatus();
                }
                Thread.sleep(100);
            }
        } finally {
            if (successOut != null) {
                successOut.flush();
                successOut.close();
            }
            channelExec.disconnect();
        }
    }

    /**
     * 创建SFTP通道
     *
     * @param session                 会话
     * @param connectionTimeoutMillis 连接超时时间
     * @return {@link ChannelSftp}
     * @throws JSchException 创建channel异常或者连接异常
     */
    private static ChannelSftp channelSftp(Session session, int connectionTimeoutMillis) throws JSchException {
        synchronized (session.getHost()) {
            if (!session.isConnected()) {
                session.connect(connectionTimeoutMillis);
            }
        }
        ChannelSftp channel = (ChannelSftp) session.openChannel("sftp");
        channel.connect(connectionTimeoutMillis);
        return channel;
    }

    /**
     * 创建执行命令的通道
     *
     * @param session                 会话
     * @param command                 命令
     * @param errorOutputStream       异常输出流
     * @param connectionTimeoutMillis 连接超时时间
     * @return {@link ChannelExec}
     * @throws JSchException 创建channel异常或者连接异常
     */
    private static ChannelExec channelExec(Session session, String command, OutputStream errorOutputStream, int connectionTimeoutMillis) throws JSchException {
        synchronized (session.getHost()) {
            if (!session.isConnected()) {
                session.connect(connectionTimeoutMillis);
            }
        }
        ChannelExec channel = (ChannelExec) session.openChannel("exec");
        channel.setCommand(command);
        channel.setInputStream(null);
        channel.setErrStream(errorOutputStream);
        channel.connect(connectionTimeoutMillis);
        return channel;
    }

    /**
     * cd目录，如果目录不存在则创建目录再cd,在一个JVM通过同步该类避免并发问题，多个JVM则需要在调用者上做分布式锁，或者允许创建一个已存在目录的错误抛出
     *
     * @param channelSftp sftp通道
     * @param remoteDir   远端文件夹
     * @throws SftpException cd目录或者mkdir异常
     */
    private static void cdOrMkdirThenCd(ChannelSftp channelSftp, String remoteDir) throws SftpException {
        remoteDir = remoteDir.replace("\\", "/");
        boolean absoluteDir = remoteDir.startsWith("/");
        String tmpDir = absoluteDir ? remoteDir.substring(1) : remoteDir;
        String[] dirs = tmpDir.split("/");
        for (int i = 0; i < dirs.length; i++) {
            String dir = i == 0 && absoluteDir ? "/" + dirs[i] : dirs[i];
            synchronized (SftpUtils.class) {
                try {
                    channelSftp.cd(dir);
                } catch (SftpException e) {
                    if (e.id == ChannelSftp.SSH_FX_NO_SUCH_FILE) {
                        channelSftp.mkdir(dir);
                        channelSftp.cd(dir);
                        continue;
                    }
                    throw e;
                }
            }
        }
    }

    /**
     * 服务器信息
     */
    @Getter
    @Setter
    public static class Server {

        private String username;

        private String password;

        private String host;

        private int port;

    }

    /**
     * SFTP日志实现类
     */
    public static class SftpLogger implements com.jcraft.jsch.Logger {

        private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(SftpLogger.class);

        private volatile LogLevel level;

        public SftpLogger(LogLevel level) {
            this.level = level;
        }

        @Override
        public boolean isEnabled(int level) {
            return level >= this.level.getLevel();
        }

        @Override
        public void log(int level, String message) {
            switch (level) {
                case Logger.DEBUG:
                    LOGGER.debug(message);
                    break;
                case Logger.WARN:
                    LOGGER.warn(message);
                    break;
                case Logger.ERROR:
                    LOGGER.error(message);
                    break;
                case Logger.FATAL:
                    break;
                default:
                    LOGGER.info(message);
            }
        }

        public void setLevel(LogLevel level) {
            this.level = level;
        }

        /**
         * 日志级别
         */
        @Getter
        public enum LogLevel {

            DEBUG(Logger.DEBUG),

            INFO(Logger.INFO),

            WARN(Logger.WARN),

            ERROR(Logger.ERROR),

            FATAL(Logger.FATAL),
            ;

            private final int level;

            LogLevel(int level) {
                this.level = level;
            }

        }

    }

}
