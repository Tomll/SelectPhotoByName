package com.autoai.packagethemelibrary;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Scanner;

/**
 * Created by dongrp on 2020/07/01
 */
public class PackageMain {

    private static File[] fs;
    private static final String path_qudao = "u_themes/";
    private static final String path_themes_json_one = "u_themes/one_theme/themes.json";
    private static final String path_themes_json_two = "u_themes/two_theme/themes.json";
    private static final String path_sn_config_json = "u_themes/sn_config.json";

    private static String new_path_sn_config_json = "qudao/sn_config.json";
    private static String new_path_100_skin = "qudao/themes/100/100.skin";
    private static String new_path_101_skin = "qudao/themes/101/101.skin";
    private static String new_path_100_preview = "qudao/themes/100/preview/";
    private static String new_path_101_preview = "qudao/themes/101/preview/";
    private static String new_path_themes_json = "qudao/themes/themes.json";
    private static String new_path_lockScreen = "qudao/themes/vuiLockScreenPreviews/";
    private static String new_path_vuiLockScreen_json = "qudao/themes/vuiLockScreen.json";

    private static String themeName_100 = "";
    private static String themeName_101 = "";
    private static String themeIntro_100 = "";
    private static String themeIntro_101 = "";
    private static String themeSize_100 = "";
    private static String themeSize_101 = "";
    private static String releaseDate = "            \"releaseDate\": \"" + System.currentTimeMillis() + "\",";

    public static void main(String[] args) {
        String p_id = inputP_ID();//产品型号
        String sn_id = inputSN_ID();//渠道号
        initOutPath(p_id, sn_id);
        String theme_number = inputThemeNumber();//主题套数

        //开始打包
        System.out.println("正在进行主题打包......\n......\n......\n......");
        try {
            if ("1".equals(theme_number)) {//一套主题打包方案
                copyFilesAndPreviewImgs_OneTheme(p_id, sn_id);
            } else if ("2".equals(theme_number)) {//两套主题打包方案
                copyFilesAndPreviewImgs_TwoTheme(p_id, sn_id);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("\n---主题打包完成,请检查新生成的qudao文件夹---\n");
        inputExit();//按任意键退出
    }

    //根据输入的产品型号，制定输出文件夹名字
    private static void initOutPath(String p_id, String sn_id) {
        new_path_sn_config_json = p_id + "_" + sn_id + "_qudao/sn_config.json";
        new_path_100_skin = p_id + "_" + sn_id + "_qudao/themes/100/100.skin";
        new_path_101_skin = p_id + "_" + sn_id + "_qudao/themes/101/101.skin";
        new_path_100_preview = p_id + "_" + sn_id + "_qudao/themes/100/preview/";
        new_path_101_preview = p_id + "_" + sn_id + "_qudao/themes/101/preview/";
        new_path_themes_json = p_id + "_" + sn_id + "_qudao/themes/themes.json";
        new_path_lockScreen = p_id + "_" + sn_id + "_qudao/themes/vuiLockScreenPreviews/";
        new_path_vuiLockScreen_json = p_id + "_" + sn_id + "_qudao/themes/vuiLockScreen.json";
    }

    //输入产品型号
    private static String inputP_ID() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("请输入产品型号:");
        String p_id = scanner.nextLine();
        if ("".equals(p_id)) {
            System.out.println("您输入产品型号为空，请重新输入:");
            inputP_ID();
        } else {
            System.out.println("您输入产品型号是:" + p_id + "\n");
        }
        return p_id;
    }

    //输入渠道号
    private static String inputSN_ID() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("请输入渠道号:");
        String sn_id = scanner.nextLine();
        if ("".equals(sn_id)) {
            System.out.println("您输入的渠道号为空，请重新输入:");
            inputSN_ID();
        } else {
            System.out.println("您输入渠道号是:" + sn_id + "\n");
        }
        return sn_id;
    }

    //输入主题套数
    private static String inputThemeNumber() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("请输入主题套数（1套输入1，两套输入2）:");
        String theme_num = scanner.nextLine();
        if (!"1".equals(theme_num) && !"2".equals(theme_num)) {
            System.out.println("您输入的主题套数不正确，请重新输入:");
            inputSN_ID();
        } else {
            System.out.println("您输入的主题套数是:" + theme_num + "\n");
        }
        return theme_num;
    }

    //输入，退出
    private static void inputExit() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("按任意键退出:");
        scanner.nextLine();
    }

    //主题打包异步任务
//    static class MyAsyncTask extends AsyncTask<String, Void, Void> {
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            System.out.println("正在进行主题打包......\n......\n......\n......");
//        }
//
//        @Override
//        protected Void doInBackground(String... strings) {
//            //主题打包
//            try {
//                copyFilesAndPreviewImgs(strings[0], strings[1]);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Void aVoid) {
//            super.onPostExecute(aVoid);
//            System.out.println("主题打包完成！");
//        }
//    }


    //预置主题U盘包的 打包逻辑:  复制skin文件、预览图文件、修改并复制json配置文件
    private static void copyFilesAndPreviewImgs_OneTheme(String p_id, String sn_id) {
        File file = new File(path_qudao);
        fs = file.listFiles();//遍历u_themes文件夹
        for (File f : fs) {
            //默认主题相关文件复制
            if (f.getName().startsWith("[默认]")) {
                File file1 = new File(f.getAbsolutePath());
                File[] fs1 = file1.listFiles();
                for (File file2 : fs1) {
                    if (file2.getName().endsWith(".skin")) {//默认主题的skin文件
                        fileCopy(file2.getAbsolutePath(), new_path_100_skin);
                        themeSize_100 = "            \"themeSize\": " + file2.length() + ",";
                    } else if (file2.getName().equals("imgs")) {//默认主题预览图文件夹
                        copy(file2.getAbsolutePath(), new_path_100_preview);
                    } else if (file2.getName().endsWith("主题说明.txt")) {//主题说明文件
                        readFileByLines1(file2.getAbsolutePath(), 100);
                    }
                }
            }
            //预置主题相关文件复制
            /*else if (f.getName().startsWith("[预置]")) {
                File file1 = new File(f.getAbsolutePath());
                File[] fs1 = file1.listFiles();
                for (File file2 : fs1) {
                    if (file2.getName().endsWith(".skin")) {//默认主题的skin文件
                        fileCopy(file2.getAbsolutePath(), new_path_101_skin);
                        themeSize_101 = "            \"themeSize\": " + file2.length() + ",";
                    } else if (file2.getName().equals("imgs")) {//默认主题预览图文件夹
                        copy(file2.getAbsolutePath(), new_path_101_preview);
                    } else if (file2.getName().endsWith("主题说明.txt")) {//主题说明文件
                        readFileByLines1(file2.getAbsolutePath(), 101);
                    }
                }
            }*/
            //锁屏相关文件复制
            else if (f.getName().equals("lockScreen")) {
                File file1 = new File(f.getAbsolutePath());
                File[] fs1 = file1.listFiles();
                for (File file2 : fs1) {
                    if (file2.getName().equals("vuiLockScreenPreviews")) {
                        copy(file2.getAbsolutePath(), new_path_lockScreen);
                    } else if (file2.getName().equals("vuiLockScreen.json")) {
                        fileCopy(file2.getAbsolutePath(), new_path_vuiLockScreen_json);
                    }
                }
            }
        }

        //修改themes.json文件并复制
        modifyThemesJsonFile(path_themes_json_one, new_path_themes_json);

        //修改sn_config.json文件并复制
        modifySnConfigJsonFile(new_path_sn_config_json, p_id, sn_id);

    }


    //预置主题U盘包的 打包逻辑:  复制skin文件、预览图文件、修改并复制json配置文件
    private static void copyFilesAndPreviewImgs_TwoTheme(String p_id, String sn_id) {
        File file = new File(path_qudao);
        fs = file.listFiles();//遍历u_themes文件夹
        for (File f : fs) {
            //默认主题相关文件复制
            if (f.getName().startsWith("[默认]")) {
                File file1 = new File(f.getAbsolutePath());
                File[] fs1 = file1.listFiles();
                for (File file2 : fs1) {
                    if (file2.getName().endsWith(".skin")) {//默认主题的skin文件
                        fileCopy(file2.getAbsolutePath(), new_path_100_skin);
                        themeSize_100 = "            \"themeSize\": " + file2.length() + ",";
                    } else if (file2.getName().equals("imgs")) {//默认主题预览图文件夹
                        copy(file2.getAbsolutePath(), new_path_100_preview);
                    } else if (file2.getName().endsWith("主题说明.txt")) {//主题说明文件
                        readFileByLines1(file2.getAbsolutePath(), 100);
                    }
                }
            }
            //预置主题相关文件复制
            else if (f.getName().startsWith("[预置]")) {
                File file1 = new File(f.getAbsolutePath());
                File[] fs1 = file1.listFiles();
                for (File file2 : fs1) {
                    if (file2.getName().endsWith(".skin")) {//默认主题的skin文件
                        fileCopy(file2.getAbsolutePath(), new_path_101_skin);
                        themeSize_101 = "            \"themeSize\": " + file2.length() + ",";
                    } else if (file2.getName().equals("imgs")) {//默认主题预览图文件夹
                        copy(file2.getAbsolutePath(), new_path_101_preview);
                    } else if (file2.getName().endsWith("主题说明.txt")) {//主题说明文件
                        readFileByLines1(file2.getAbsolutePath(), 101);
                    }
                }
            }
            //锁屏相关文件复制
            else if (f.getName().equals("lockScreen")) {
                File file1 = new File(f.getAbsolutePath());
                File[] fs1 = file1.listFiles();
                for (File file2 : fs1) {
                    if (file2.getName().equals("vuiLockScreenPreviews")) {
                        copy(file2.getAbsolutePath(), new_path_lockScreen);
                    } else if (file2.getName().equals("vuiLockScreen.json")) {
                        fileCopy(file2.getAbsolutePath(), new_path_vuiLockScreen_json);
                    }
                }
            }
        }

        //修改themes.json文件并复制
        modifyThemesJsonFile(path_themes_json_two, new_path_themes_json);

        //修改sn_config.json文件并复制
        modifySnConfigJsonFile(new_path_sn_config_json, p_id, sn_id);

    }


    //以行为单位读取文件内容
    private static void readFileByLines1(String filePath, int id) {
        BufferedReader reader = null;
        try {
            //Log.i("dongrp", "以行为单位读取文件内容，一次读一行");
            InputStreamReader fReader = new InputStreamReader(new FileInputStream(filePath), "UTF-8");
            reader = new BufferedReader(fReader);
            String tempString;
            //int line = 1;
            //一次读一行，读入null时文件结束
            while ((tempString = reader.readLine()) != null) {
                tempString = tempString.trim();
                //把当前行号显示出来
                //Log.i("dongrp", "line " + line + ": " + tempString);
                if (tempString.contains("主题名称：")) {
                    String themeName = tempString.replace("主题名称：", "");
                    String json_theme_name = "            \"themeName\": \"" + themeName + "\",";
                    if (id == 100) {
                        themeName_100 = json_theme_name;
                    } else if (id == 101) {
                        themeName_101 = json_theme_name;
                    }
                } else if (tempString.contains("主题说明：")) {
                    String themeIntro = tempString.replace("主题说明：", "");
                    String json_theme_intro = "            \"intro\": \"" + themeIntro + "\",";
                    if (id == 100) {
                        themeIntro_100 = json_theme_intro;
                    } else if (id == 101) {
                        themeIntro_101 = json_theme_intro;
                    }
                } else if (tempString.contains("主题名称:")) {
                    String themeName = tempString.replace("主题名称:", "");
                    String json_theme_name = "            \"themeName\": \"" + themeName + "\",";
                    if (id == 100) {
                        themeName_100 = json_theme_name;
                    } else if (id == 101) {
                        themeName_101 = json_theme_name;
                    }
                } else if (tempString.contains("主题说明:")) {
                    String themeIntro = tempString.replace("主题说明:", "");
                    String json_theme_intro = "            \"intro\": \"" + themeIntro + "\",";
                    if (id == 100) {
                        themeIntro_100 = json_theme_intro;
                    } else if (id == 101) {
                        themeIntro_101 = json_theme_intro;
                    }
                }
                //line++;
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


    //修改themes.json文件
    private static void modifyThemesJsonFile(String path, String newPath) {
        String temp;
        try {
            File file = new File(path);
            FileInputStream fis = new FileInputStream(file);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            StringBuffer buf = new StringBuffer();

            int line = 1;
            // 保存该行前面的内容
            while ((temp = br.readLine()) != null) {
                switch (line) {
                    case 5:
                        temp = themeName_100;
                        break;
                    case 6:
                        temp = themeIntro_100;
                        break;
                    case 8:
                        temp = themeSize_100;
                        break;
                    case 19:
                        temp = releaseDate;
                        break;
                    case 25:
                        temp = themeName_101;
                        break;
                    case 26:
                        temp = themeIntro_101;
                        break;
                    case 28:
                        temp = themeSize_101;
                        break;
                    case 39:
                        temp = releaseDate;
                        break;
                }

//                if(isMath){
//                    buf = buf.append(key+"="+newValue);
//                }else{
                buf = buf.append(temp);
//                }
                buf = buf.append(System.getProperty("line.separator"));
                line++;
            }

            br.close();
            File file1 = new File(newPath);
            FileOutputStream fos = new FileOutputStream(file1);
            PrintWriter pw = new PrintWriter(fos);
            pw.write(buf.toString().toCharArray());
            pw.flush();
            pw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //修改sn_config.json文件
    private static void modifySnConfigJsonFile(String newPath, String p_id, String sn_id) {
        String temp;
        try {
            File file = new File(PackageMain.path_sn_config_json);
            FileInputStream fis = new FileInputStream(file);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            StringBuffer buf = new StringBuffer();

            int line = 1;
            // 保存该行前面的内容
            while ((temp = br.readLine()) != null) {
                switch (line) {
                    case 2:
                        temp = "\"sn_id\": \"" + sn_id + "\",";
                        break;
                    case 3:
                        temp = "\"p_id\": \"" + p_id + "\"";
                        break;
                }

                buf = buf.append(temp);
                buf = buf.append(System.getProperty("line.separator"));
                line++;
            }

            br.close();
            File file1 = new File(newPath);
            FileOutputStream fos = new FileOutputStream(file1);
            PrintWriter pw = new PrintWriter(fos);
            pw.write(buf.toString().toCharArray());
            pw.flush();
            pw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    //文件、文件夹递归复制方法
    private static void copy(String src, String des) {
        try {
            //初始化文件复制
            File file1 = new File(src);
            //把文件里面内容放进数组
            File[] fs = file1.listFiles();
            //初始化文件粘贴
            File file2 = new File(des);
            //判断是否有这个文件有不管没有创建
            if (!file2.exists()) {
                file2.mkdirs();
            }
            //遍历文件及文件夹
            for (File f : fs) {
                if (f.isFile()) {
                    //文件
                    fileCopy(f.getPath(), des + f.getName()); //调用文件拷贝的方法
                } else if (f.isDirectory()) {
                    //文件夹
                    copy(f.getPath(), des + f.getName());//继续调用复制方法，递归的地方,自己调用自己的方法,就可以复制文件夹的文件夹了
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    /**
     * 文件复制的具体方法
     */
    private static void fileCopy(String src, String des) {
        //若父目录不存在，先创建父目录
        File file = new File(des);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        //io流固定格式
        BufferedInputStream bis;
        BufferedOutputStream bos;
        try {
            bis = new BufferedInputStream(new FileInputStream(src));
            bos = new BufferedOutputStream(new FileOutputStream(des));

            int i;//记录获取长度
            byte[] bt = new byte[2014];//缓冲区
            while ((i = bis.read(bt)) != -1) {
                bos.write(bt, 0, i);
            }
            //关闭流
            bis.close();
            bos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
