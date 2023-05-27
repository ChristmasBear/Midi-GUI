package dev.christmasbear;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.sound.midi.*;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class GUI {
    private static JFrame frame = new JFrame();
    static boolean hiHatClosed = false;
    static int hiHatClosedThreshold = 80;

    static int hiHatMax = 90;
    static int hiHatPos = 0;
    static int graphMax = 100;
    static int updateMs = 10;

    static int x = GUILayout.uiLayout.size();
    static int y = GUILayout.uiLayout.get(0).length;
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    static DefaultCategoryDataset[][] datasets = new DefaultCategoryDataset[x][y];

    static JFreeChart[][] charts = new JFreeChart[x][y];
    static JLabel[][] labels = new JLabel[x][y];
    static float[][] lastInput = new float[x][y];
    static Color[][] colours = new Color[x][y];
    static JPanel panel = new JPanel();

    public GUI() {
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        panel.setLayout(new GridLayout(y - 1, x - 1));

        for (int i = 0; i < GUILayout.uiLayout.size(); i++) {
            for (int j = 0; j < y; j++) {
                /*indicators[i][j].setStringPainted(true);
                indicators[i][j].setString(MidiInputs.idToName.get(GUILayout.uiLayout.get(i)[j]));
                indicators[i][j].setValue(0);
                panel.add(indicators[i][j]);*/
                charts[i][j].getCategoryPlot().getRangeAxis().setRange(0, 127);
                BufferedImage img = charts[i][j].createBufferedImage(500, 270);
                labels[i][j].setIcon(new ImageIcon(img));
                panel.add(labels[i][j]);
            }
        }

        frame.add(panel, BorderLayout.CENTER);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle("this is so cool");
        frame.pack();
        frame.setVisible(true);
    }

    public static boolean containsInt(final int[] arr, final int key) {
        return Arrays.stream(arr).anyMatch(i -> i == key);
    }

    static HashMap<String, Integer> counters = new HashMap<>();
    private static void updateChart(String id, float value) {
        int[] pos = GUILayout.getPos(id);
        if (!counters.containsKey(id)) {
            counters.put(id, 0);
        }
        if (counters.get(id) > graphMax) {
            datasets[pos[0]][pos[1]].removeColumn(0);
        }
        counters.put(id, counters.get(id) + 1);
        JFreeChart chart = charts[pos[0]][pos[1]];
        datasets[pos[0]][pos[1]].addValue(value, "", Integer.toString(counters.get(id)));
        chart.setBackgroundPaint(colours[pos[0]][pos[1]]);
        BufferedImage img = chart.createBufferedImage(500, 270);
        labels[pos[0]][pos[1]].setIcon(new ImageIcon(img));
        labels[pos[0]][pos[1]].repaint();
        Decay decay = GUILayout.decay.get(id);
        if (!decay.isZero()) {
            float decayAmount = decay.getY(lastInput[pos[0]][pos[1]]);
            lastInput[pos[0]][pos[1]] -= decayAmount;
        } else if (value > 0 && !id.equals("hhp")) {
            lastInput[pos[0]][pos[1]] = 0;
        }
    }

    public static void main(String[] args) {
        for (int i = 0; i < GUILayout.uiLayout.size(); i++) {
            for (int j = 0; j < GUILayout.uiLayout.get(0).length; j++) {
                datasets[i][j] = new DefaultCategoryDataset();
                charts[i][j] = ChartFactory.createLineChart(MidiInputs.idToName.get(GUILayout.uiLayout.get(i)[j]), "", "", datasets[i][j], PlotOrientation.VERTICAL, false, false, false);
                labels[i][j] = new JLabel();
                lastInput[i][j] = 0;
                colours[i][j] = Color.WHITE;
            }
        }

        new GUI();
        try {
            MidiDevice.Info[] infos = MidiSystem.getMidiDeviceInfo();
            MidiDevice device = MidiSystem.getMidiDevice(infos[5]);
            device.open();
            device.getTransmitter().setReceiver(new Receiver() {
                @Override
                public void send(MidiMessage message, long timeStamp) {
                    if (message instanceof ShortMessage) {
                        ShortMessage sm = (ShortMessage) message;
                        int data = sm.getData1();

                        if (sm.getCommand() == 176) {
                            if (sm.getData1() == 4) {
                                int[] hhpPos = GUILayout.getPos("hhp");
                                hiHatClosed = sm.getData2() > hiHatClosedThreshold;
                                hiHatPos = sm.getData2();
                                lastInput[hhpPos[0]][hhpPos[1]] = hiHatPos;
                            }
                        } else if (sm.getCommand() == 144) {
                            System.out.println("note on: " + sm.getData1());
                            int intensity = sm.getData2();
                            if (containsInt(MidiInputs.idToMidi.get("hhc"), data)) {
                                int[] hhcPos = GUILayout.getPos("hhc");
                                int[] hhoPos = GUILayout.getPos("hho");
                                if (hiHatClosed) {
                                    lastInput[hhcPos[0]][hhcPos[1]] = intensity;
                                    colours[hhcPos[0]][hhcPos[1]] = Color.GREEN;
                                } else {
                                    lastInput[hhoPos[0]][hhoPos[1]] = intensity;
                                    colours[hhoPos[0]][hhoPos[1]] = Color.GREEN;
                                }
                            } else {
                                for (int i = 0; i < GUILayout.uiLayout.size(); i++) {
                                    for (int j = 0; j < GUILayout.uiLayout.get(0).length; j++) {
                                        if (containsInt(MidiInputs.idToMidi.get(GUILayout.uiLayout.get(i)[j]), data)) {
                                            lastInput[i][j] = intensity;
                                            colours[i][j] = Color.GREEN;
                                        }
                                    }
                                }
                            }
                        } else if (sm.getCommand() == 128) {
                            if (containsInt(MidiInputs.idToMidi.get("hhp"), data)) {
                                int[] hhoPos = GUILayout.getPos("hho");
                                lastInput[hhoPos[0]][hhoPos[1]] = 0;
                                colours[hhoPos[0]][hhoPos[1]] = Color.WHITE;
                            }
                            for (int i = 0; i < GUILayout.uiLayout.size(); i++) {
                                for (int j = 0; j < GUILayout.uiLayout.get(0).length; j++) {
                                    if (containsInt(MidiInputs.idToMidi.get(GUILayout.uiLayout.get(i)[j]), data)) {
                                        colours[i][j] = Color.WHITE;
                                    }
                                }
                            }
                        }
                    }
                }

                @Override
                public void close() {
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Ready");
        scheduler.scheduleAtFixedRate(() -> {
            for (int i = 0; i < GUILayout.uiLayout.size(); i++) {
                for (int j = 0; j < GUILayout.uiLayout.get(0).length; j++) {
                    updateChart(GUILayout.uiLayout.get(i)[j], lastInput[i][j]);
                    frame.repaint();
                }
            }
        }, 0, updateMs, TimeUnit.MILLISECONDS);
    }
}