<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.io.*,java.util.*,javax.servlet.*,javax.servlet.http.*" %>
<%@ page import="java.rmi.ServerException" %>
<html>
<head>
    <title></title>
</head>
<body>
<%

    String rootPath;
    //声明文件读入类
    DataInputStream in = null;
    FileOutputStream fileOut = null;

    rootPath = "E:\\bistugame\\img2\\";
    //取得客户端上传的数据类型
    String contentType = request.getContentType();
    try {
        if (contentType.indexOf("multipart/form-data") >= 0) {
            //读入上传数据
            in = new DataInputStream(request.getInputStream());
            int formDataLength = request.getContentLength();
            out.println(formDataLength);
            //保存上传文件的数据
            byte dataBytes[] = new byte[formDataLength];
            int byteRead = 0;
            int totalBytesRead = 0;
            //上传的数据保存在byte数组里面
            while (totalBytesRead < formDataLength) {
                byteRead = in.read(dataBytes, totalBytesRead, formDataLength);
                totalBytesRead += byteRead;
            }
            //根据byte数组创建字符串
            String file = new String(dataBytes, "utf-8");
            //取得上传数据的文件名
            String saveFile = file.substring(file.indexOf("filename=\"") + 10);
            saveFile = saveFile.substring(0, saveFile.indexOf("\n"));
			out.println(saveFile);
			String sss=saveFile.substring(0, saveFile.indexOf("\""));
			String ssss=saveFile.substring(0, saveFile.lastIndexOf("\\"));
			out.println(sss);
            saveFile = saveFile.substring(saveFile.lastIndexOf("\\") + 1, saveFile.indexOf("\""));
            int lastIndex = contentType.lastIndexOf("=");
            //取得数据的分隔字符串
            String boundary = contentType.substring(lastIndex + 1, contentType.length());
            //创建保存路径的文件名
			File fileDirs = new File(rootPath+ssss);
            if (!fileDirs.exists()) {
                fileDirs.mkdirs();
            }
            String fileName = rootPath + sss;
			out.println(fileName);
            int pos;
            pos = file.indexOf("filename = \"");
            pos = file.indexOf("\n", pos) + 1;
            pos = file.indexOf("\n", pos) + 1;
            pos = file.indexOf("\n", pos) + 1;
            int boundaryLocation = file.indexOf(boundary, pos) - 4;
            //取得文件数据的开始的位置
            int startPos = ((file.substring(0, pos)).getBytes()).length;
            int endPos = ((file.substring(0, boundaryLocation)).getBytes()).length;
            File checkFile = new File(fileName);
            if (checkFile.exists()) {
                out.println("<p>" + saveFile + "文件已经存在.</p>");
                return;
            }
            //检查上传文件的目录是否存在
            File fileDir = new File(rootPath);
            if (!fileDir.exists()) {
                fileDir.mkdirs();
            }
            //创建文件的输出类
            fileOut = new FileOutputStream(fileName);
            //保存文件的数据
            fileOut.write(dataBytes, startPos, (endPos - startPos));
            fileOut.close();
            out.print("<b>文件上传成功</b>");
        } else {
            String content = request.getContentType();
            out.print("上传的文件类型是" + content + "类型的，请上传目录mutipart/form-data类型的文件");
        }
    } catch (Exception ex) {
        throw new ServerException(ex.getMessage());
    }
%>
</body>
</html>