/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bdo_boss_timer;

import java.awt.AWTException;
import java.awt.Button;
import java.awt.Checkbox;
import java.awt.Choice;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Image;
import java.awt.Label;
import java.awt.MenuItem;
import java.awt.Panel;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JSlider;

/**
 *
 * @author Hate
 */
public class TrayMode {
    private int GMT = 0;
    private TrayIcon ti;
    private static final Image iconImageLoc = Toolkit.getDefaultToolkit().getImage("week/Gmark.png");
    private ArrayList<BossListItem> sh = new ArrayList<BossListItem>();
    private MyThread startTimer = new MyThread();
    private Alert alert = new Alert();;
    private boolean StartWithWindows = false;
    private float Volume = (float) 1;
    
    public TrayMode() throws AWTException, FileNotFoundException, ParseException, IOException {
        init();
        BossTimer(false);
    }
    
    private void init() throws AWTException, FileNotFoundException, IOException, ParseException{
        LoadConfig();
        System.out.println("First");
        SystemTray tray = SystemTray.getSystemTray();
        System.out.println("Second");
        PopupMenu menu = new PopupMenu();
        
        MenuItem settings = new MenuItem("Настройки");
        MenuItem exit = new MenuItem("Выход");
        MenuItem next = new MenuItem("Ближайший босс");
        
        menu.add(settings);
        menu.add(next);
        menu.add(exit);
        
        ti = new TrayIcon(iconImageLoc, "BDO Boss", menu);
        
        tray.add(ti);
        System.out.println("Third");
        
        exit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                System.exit(0);
            }
        });
        settings.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                SettingsInit();
            }
        });
        next.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                BossTimer(false);
            }
        });
        
        ti.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(e.getClickCount() == 2){
                    BossTimer(false);
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                
            }

            @Override
            public void mouseExited(MouseEvent e) {
                
            }});
        
        getBossSchedule(0);
        startTimer.start();
    }
    
    private void getBossSchedule(int day){
        FileInputStream fis = null;
        try {
            LocalDateTime now = LocalDateTime.now();
            now = now.plusDays(-1);
            now = now.plusDays(day);
            DateTimeFormatter dayFormat = DateTimeFormatter.ofPattern("E");
            DateTimeFormatter HMFormat = DateTimeFormatter.ofPattern("E H:mm", Locale.US);
            fis = new FileInputStream("week/" + dayFormat.format(now) + ".txt");
            BufferedReader br = new BufferedReader(new InputStreamReader(fis, "cp1251"));
            String temp;
            while((temp = br.readLine()) != null){
                BossListItem boss = new BossListItem(temp, dayFormat.format(now));
                boss.minusHour(3);
                boss.plusHour(GMT);
                if(boss.isAfter(LocalDateTime.now())){
                    sh.add(boss);
                }
            }
            if(sh.size() < 1){
                day++;
                getBossSchedule(day);
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(TrayMode.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(TrayMode.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(TrayMode.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                fis.close();
            } catch (IOException ex) {
                Logger.getLogger(TrayMode.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    private void BossTimer(boolean sound){
        ti.displayMessage(null, "Босс: " + sh.get(0).getName() + " через " + sh.get(0).timeForNext(), TrayIcon.MessageType.INFO);
        if(sound){
           alert = new Alert();
           alert.start();
        }
    }
    
    private void SettingsInit(){
        Frame root = new Frame("Настройки");
        Panel main = new Panel();
        root.add(main);
        Label GMTSelectorText = new Label("GMT");
        Choice GMTSelector = new Choice();
        Checkbox start = new Checkbox("Запуск с Windows");
        start.setState(StartWithWindows);
        JSlider volume = new JSlider(0, 200);
        Label volimeLabel = new Label("Громкость оповещения");
        volume.setValue((int) (Volume * 100));
        
        main.add(GMTSelectorText);
        main.add(GMTSelector);
        main.add(start);
        main.add(volimeLabel);
        main.add(volume);
        
        for(int i = -12; i <= 12; i++){
            GMTSelector.add(String.valueOf(i));
            GMTSelector.select(String.valueOf(GMT));
        }
        root.setLocationRelativeTo(null);
        root.setVisible(true);
        root.setSize(215, 165);
        root.setResizable(false);
        root.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {
            }

            @Override
            public void windowClosing(WindowEvent e) {
                root.setVisible(false);
            }

            @Override
            public void windowClosed(WindowEvent e) {
            }

            @Override
            public void windowIconified(WindowEvent e) {
            }

            @Override
            public void windowDeiconified(WindowEvent e) {
            }

            @Override
            public void windowActivated(WindowEvent e) {
            }

            @Override
            public void windowDeactivated(WindowEvent e) {
            }
        });
        main.setLayout(new FlowLayout(FlowLayout.LEFT));
        Button save = new Button("Сохранить");
        Button cancel = new Button("Отмена");
        
        main.add(save);
        main.add(cancel);
        
        cancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                LoadConfig();
                Restart();
                root.setVisible(false);
            }
        });
        
        save.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SaveConfig();
                Restart();
                save.setEnabled(false);
            }
        });
        
        GMTSelector.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                GMT = Integer.parseInt(GMTSelector.getItem(GMTSelector.getSelectedIndex()));
                Restart();
                save.setEnabled(true);
            }
        });
        
        volume.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
            }

            @Override
            public void mousePressed(MouseEvent e) {
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if(volume.getValue() <= 0){
                    Volume = (float) 0.001;
                } else {
                    Volume = (float)(volume.getValue()) / 100;
                    System.out.println((float)(volume.getValue()) / 100);
                }
                alert.interrupt();
                alert = new Alert();
                alert.start();
                save.setEnabled(true);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
            }

            @Override
            public void mouseExited(MouseEvent e) {
            }
        });
        
        start.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                File file = new File("startWithWindows.bat");
                if(!file.exists()){
                    try {
                        file.createNewFile();
                    } catch (IOException ex) {
                        Logger.getLogger(TrayMode.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                if(start.getState()){
                    try {
                        new Thread(() -> {
                            try {
                                File file1 = new File("autorun.bat");
                                file1.createNewFile();
                                PrintWriter pw = new PrintWriter(file1);
                                pw.write("@Echo off\n");
                                System.out.println(file1.getAbsolutePath());
                                pw.write("cd /d " + file1.getAbsolutePath().substring(0, file1.getAbsolutePath().lastIndexOf("\\")) + "\n");
                                pw.write("start bin\\javaw.exe -jar BDO_Boss_Timer.jar autorun");
                                pw.flush();
                                pw.close();
                            }catch (IOException ex) {
                                Logger.getLogger(TrayMode.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }).start();
                        String path = file.getAbsolutePath().substring(0, file.getAbsolutePath().lastIndexOf("\\"));
                        System.out.println(path);
                        PrintWriter pw = new PrintWriter("startWithWindows.bat");
                        pw.write("REG ADD \"HKCU\\SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Run\" /v \"BDO_Boss_Timer\" /t REG_SZ /d \"" + path + "\\autorun.bat");
                        pw.flush();
                        pw.close();
                    } catch (FileNotFoundException ex) {
                        Logger.getLogger(TrayMode.class.getName()).log(Level.SEVERE, null, ex);
                    } 
                }else{
                    try {
                        new Thread(() -> {
                            File file1 = new File("autorun.bat");
                            file1.delete();
                        }).start();
                        String path = file.getAbsolutePath().substring(0, file.getAbsolutePath().lastIndexOf("\\"));
                        System.out.println(path);
                        PrintWriter pw = new PrintWriter("startWithWindows.bat");
                        pw.write("REG DELETE \"HKCU\\SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Run\" /v \"BDO_Boss_Timer\"" + " /f");
                        pw.flush();
                        pw.close();
                    } catch (FileNotFoundException ex) {
                        Logger.getLogger(TrayMode.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                try {
                    Process process = new ProcessBuilder("startWithWindows.bat").start();
                    Thread.sleep(100);
                    process.destroy();
                } catch (IOException ex) {
                    Logger.getLogger(TrayMode.class.getName()).log(Level.SEVERE, null, ex);
                } catch (InterruptedException ex) {
                    Logger.getLogger(TrayMode.class.getName()).log(Level.SEVERE, null, ex);
                }
                file.delete();
                StartWithWindows = start.getState();
                SaveConfig();
                save.setEnabled(false);
            }
        });
    }
    
    class MyThread extends Thread{
    
        @Override
        public void run(){
            while(true){
                try {
                    if(sh.get(0).getDeltaTimeToMilli(10) != 0){
                        sleep(sh.get(0).getDeltaTimeToMilli(15));
                        BossTimer(true);
                    }
                    if(sh.get(0).getDeltaTimeToMilli(5) != 0){
                        sleep(sh.get(0).getDeltaTimeToMilli(10));
                        BossTimer(true);
                    }
                    sleep(sh.get(0).getDeltaTimeToMilli(5));
                    BossTimer(true);
                    sleep(sh.get(0).getDeltaTimeToMilli(0));
                    sh.remove(0);
                    if(sh.size() == 0){
                        getBossSchedule(1);
                    }
                    BossTimer(false);
                } catch (InterruptedException ex) {
                    System.out.println("Поток завершён!");
                    break;
                }

            }
        }
    }
    
    private void SaveConfig(){
        try {
            FileWriter fw = new FileWriter("week/Config.cfg");
            fw.write("GMT " + GMT);
            fw.write("\n");
            fw.write("Volume " + Volume);
            fw.write("\n");
            fw.write("StartWithWindows " + StartWithWindows);
            fw.flush();
            fw.close();
        } catch (IOException | NullPointerException ex) {
            GMT = 0;
            Volume = (float) 1.0;
            StartWithWindows = false;
            ti.displayMessage(null, "Ошибка загрузки конфига. Применены настройки \"По умолчанию\"", TrayIcon.MessageType.ERROR);
        }
    }
    
    private void LoadConfig(){
        FileInputStream fis = null;
        try {
            fis = new FileInputStream("week/Config.cfg");
            BufferedReader br = new BufferedReader(new InputStreamReader(fis, "cp1251"));
            GMT = Integer.parseInt(br.readLine().split(" ")[1]);
            Volume = Float.parseFloat(br.readLine().split(" ")[1]);
            StartWithWindows = Boolean.valueOf(br.readLine().split(" ")[1]);
        } catch (FileNotFoundException | UnsupportedEncodingException ex) {
            System.out.println("Error 1");
        } catch (NullPointerException | IOException ex) {
            System.out.println("Error 2");
        } finally {
            try {
                fis.close();
            } catch (NullPointerException | IOException ex) {
                System.out.println(new File("week/Config.cfg").getAbsolutePath());
            }
        }
    }
    class Alert extends Thread{
        @Override
        public void run(){
             try {
                File soundFile = new File("week/Sound.wav");
                AudioInputStream ais = AudioSystem.getAudioInputStream(soundFile);
                Clip clip = AudioSystem.getClip();
                clip.open(ais);
                clip.setFramePosition(0);
                ((FloatControl)clip.getControl(FloatControl.Type.MASTER_GAIN)).setValue(20f * (float) Math.log10(Volume));
                clip.start();
                Thread.sleep(clip.getMicrosecondLength()/1000);
                clip.stop();
                clip.close();
                interrupt();
            } catch (LineUnavailableException | UnsupportedAudioFileException | IOException | InterruptedException ex) {
                
            }
        }
    }
    
    private void Restart(){
        startTimer.interrupt();
        sh.removeAll(sh);
        getBossSchedule(0);
        startTimer = new MyThread();
        startTimer.start();
        BossTimer(false);
    }
}


