package autoai.com.aaaexceltest;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.FontRequest;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public static final String nameFile = "/storage/emulated/0/360AAA/name.txt";//姓名列表文件
    public static final String headPath = "/storage/emulated/0/360AAA/head/";//原始头像目录
    public static final String headPathNew = "/storage/emulated/0/360AAA/headNew/";//头像筛选后复制到的新目录
    ArrayList<User> userList = new ArrayList<>();
    File[] fs;//原始头像数据--文件数组

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //检查版本是否大于M
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 169);
            } else {//权限已经申请
                ergodicFile();//先遍历原始头像目录
                readFileByLines(nameFile);//读出员工信息，存入User集合
                selectHeadImage();//筛选并复制头像到新目录

            }
        }

    }

    /**
     * 遍历文件夹
     */
    public void ergodicFile() {
        //String path = "D:\\JAVA";		//要遍历的路径
        File file = new File(headPath);        //获取其file对象
        fs = file.listFiles();    //遍历path下的文件和目录，放在File数组中
        //for(File f:fs){					//遍历File[]数组
        //    if(!f.isDirectory())		//若非目录(即文件)，则打印
        //        System.out.println(f);
        //}
    }

    /**
     * Java使用FileReader(file)、readLine()读取文件，以行为单位，一次读一行，一直读到null时结束，每读一行都显示行号。
     *
     * @param filePath：文件的绝对路径
     */
    public void readFileByLines(String filePath) {
        File file = new File(filePath);
        BufferedReader reader = null;
        try {
            Log.i("dongrp", "以行为单位读取文件内容，一次读一行");
            reader = new BufferedReader(new FileReader(file));
            String tempString;
            int line = 1;
            //一次读一行，读入null时文件结束
            while ((tempString = reader.readLine()) != null) {
                //把当前行号显示出来
                //Log.i("dongrp", "line " + line + ": " + tempString);

                User user = new User();
                String[] split = tempString.split("\t");
                //Log.i("dongrp", "split[0] : " + split[0]);//姓名
                //Log.i("dongrp", "split[1] : " + split[1]);//部门
                user.setName(split[0]);
                user.setDepartment(split[1]);

                //如果对应的部门文件夹不存在，则创建
                File file_b = new File(headPathNew + split[1]);
                if (!file_b.exists()) {
                    file_b.mkdirs();
                }

                userList.add(user);
                line++;
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    /**
     * 筛选出目标头像，复制到目标目录
     */
    public void selectHeadImage() {
        for (File f : fs) {//外循环遍历 原始头像文件数组
            String fileName = f.getName();
            for (User user : userList) {//内循环遍历 部门名单集合
                if (fileName.contains(user.name)) {
                    copyFile(f.getAbsolutePath(), headPathNew + user.department + File.separator + fileName);
                }
            }
        }


    }

    /**
     * 复制文件(FileChannel的transferTo())
     * sourceFilePath：源文件路径
     * destFilePath：目标文件路径
     * <p>
     * 实际复制的字节数，如果文件、目录不存在、文件为null或者发生IO异常，返回-1
     */
    public static boolean copyFile(String sourceFilePath, String destFilePath) {
        //long l = System.currentTimeMillis();
        long size = 0;//真实复制的字节长度
        long length = -1;//原文件的字节长度
        File srcFile = new File(sourceFilePath);
        File destFile = new File(destFilePath);
        File destDir = new File(destFile.getParent());//根据destFile
        try {
            RandomAccessFile raf = new RandomAccessFile(srcFile, "rw");
            length = raf.length();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (!srcFile.exists()) {
            return false;
        } else if (!destDir.exists()) {
            destDir.mkdirs();
        } else {
            try {
                FileChannel fcin = new FileInputStream(srcFile).getChannel();
                FileChannel fcout = new FileOutputStream(new File(destDir, srcFile.getName())).getChannel();
                size = fcin.size();
                fcin.transferTo(0, fcin.size(), fcout);
                fcin.close();
                fcout.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //Log.d("XorEncryptionUtil", "length%%%%%%%%%:" + length);
        //Log.d("XorEncryptionUtil", "size%%%%%%%%%%%:" + size);
        //long l1 = System.currentTimeMillis();
        //Log.d("XorEncryptionUtil", "copy 文件耗时(l1-l):" + (l1 - l));
        return length == size;
    }


    //员工 javaBean
    class User {
        String name;
        String department;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDepartment() {
            return department;
        }

        public void setDepartment(String department) {
            this.department = department;
        }
    }

}