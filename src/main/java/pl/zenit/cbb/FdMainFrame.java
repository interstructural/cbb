package pl.zenit.cbb;

import pl.zenit.cbb.d2.Points2d;
import pl.zenit.cbb.d2.Rect2d;
import pl.zenit.cbb.renderer.RendererCameraAnimator;
import pl.zenit.cbb.renderer.BinaryFunctions;
import pl.zenit.cbb.renderer.BoundaryCondition;
import pl.zenit.cbb.renderer.Coloring;
import pl.zenit.cbb.renderer.Equation;
import pl.zenit.cbb.renderer.UnaryFunctions;
import pl.zenit.cbb.renderer.Renderer;

import java.awt.event.KeyEvent;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;
import javax.swing.JFrame;

import pl.zenit.cbb.util.Threads;
import pl.zenit.cbb.util.Throttler;

public final class FdMainFrame extends JFrame {

    private volatile boolean currentRun = false;
    private volatile boolean nextRun = false;
    private volatile boolean initiated = false;
    private final Map<String, Equation> functions = new TreeMap<>();
    private final Map<String, BoundaryCondition> boundaryConditions = new TreeMap<>();
    private final Map<String, Function<Integer, Coloring>> colorings = new TreeMap<>();
    private final Renderer renderer = new Renderer();
    private final RendererCameraAnimator camera = new RendererCameraAnimator(renderer);
    private final Throttler progressThrottler = new Throttler();
      
    FdMainFrame() {
        this.setResizable(true);

        initComponents();
        setLocationRelativeTo(null);
        setTitle(Main.NAME);
        jLabelParam.setText(Main.NAME);
        jCheckBoxMultithread.setSelected(true);

        jSliderParamStateChanged(null);
        jSliderIterationDepthStateChanged(null);
        buildFunctions();
        buildBoundaryConditions();
        buildColorings();
        renderer.setProgressMessage(this::showProgress);
        initiated = true;
        go();

        Rect2d base = new Rect2d(100, 100, 500, 200);
        Rect2d scaled = new Rect2d(base.left, base.top, base.left + base.width() / 2, base.top + base.width() / 2);

        System.out.println(base.points.center());
        System.out.println(scaled.points.center());
        System.out.println(Points2d.getTravel(base.points.center(), scaled.points.center()));

        /* 

        oto jest pomnik rysowania funkcji
        i kminy zaklętej w zrozumieniu
        rysowania zadanych współrzędnych
        o określonym powiększeniu

        int X = 3; //SCALE
        int BL = 100;
        int BR = 500;
        int BW = BR - BL;
        int SL = BL;
        int SR = BL + BW / X;
        int SW = SR - SL;
        int BC = BL + BW / 2;
        int SC = SL + SW / 2;
        int Y = SC - BC;

        Y = (SL + SW / 2) - (BL + BW / 2);
        Y = SL + SW / 2 - BL - BW / 2;
        Y = BL + SW / 2 - BL - BW / 2;
        Y = SW / 2 - BW / 2;

        Y = (SR - SL) / 2 - (BR - BL) / 2;
        Y = ((SR - SL) - (BR - BL)) / 2;
        Y = (SR - SL - BR + BL) / 2;
        Y = (SR - BL - BR + BL) / 2;
        Y = (SR - BR) / 2;
        Y = (SR - BR) / 2;

        Y = ((BL + BW / X) - BR) / 2;
        Y = (BL + BW / X) / 2 - BR / 2;
        Y = BL / 2 + BW / (2 * X) - BR / 2;

        Y = (BL + (BR - BL) / X - BR) / 2;
        Y = BL / 2 + BR / (2 * X) - BL / (2 * X) - BR / 2;
        Y = BL / 2 - BL / (2 * X) - BR / 2 + BR / (2 * X);
        Y = BL / 2 - BR / 2 + BR / (2 * X) - BL / (2 * X);
        Y = (BL - BR) / 2 + (BR - BL) / (2 * X);        
        */
    }

      private void buildFunctions() {
        functions.clear();
        new UnaryFunctions(this::arg).forEach((s, f) -> {
            Equation eq = Equation.unary(f);
            functions.put(s, eq);
        });
        functions.putAll(new BinaryFunctions(this::arg));

        jComboBoxFunctions.removeAllItems();
        functions.forEach((k, v) -> jComboBoxFunctions.addItem(k));
        jComboBoxFunctions.setSelectedIndex(0);
    }
      
      private void buildBoundaryConditions() {
            boundaryConditions.clear();
            boundaryConditions.put("DIFF 0.001", BoundaryCondition.difference(0.001f));
            boundaryConditions.put("DIFF 0.01", BoundaryCondition.difference(0.01f));
            boundaryConditions.put("DIFF 0.1", BoundaryCondition.difference(0.1f));
            boundaryConditions.put("DIFF 0.2", BoundaryCondition.difference(0.2f));
            boundaryConditions.put("DIFF 0.5", BoundaryCondition.difference(0.5f));
            
            boundaryConditions.put("DIFF 1", BoundaryCondition.difference(1));
            boundaryConditions.put("DIFF 2", BoundaryCondition.difference(2));
            boundaryConditions.put("DIFF 5", BoundaryCondition.difference(5));
            
            boundaryConditions.put("LIMIT 1", BoundaryCondition.absoluteLimit(1));
            boundaryConditions.put("LIMIT 2", BoundaryCondition.absoluteLimit(2));
            boundaryConditions.put("LIMIT 5", BoundaryCondition.absoluteLimit(5));

            boundaryConditions.put("MANDELBROT 0.1", BoundaryCondition.mandelbrotLimit(0.1));
            boundaryConditions.put("MANDELBROT 2", BoundaryCondition.mandelbrotLimit(2));
            boundaryConditions.put("MANDELBROT 3", BoundaryCondition.mandelbrotLimit(3));
            boundaryConditions.put("MANDELBROT DEF", BoundaryCondition.mandelbrotLimit(4));
            boundaryConditions.put("MANDELBROT 5", BoundaryCondition.mandelbrotLimit(5));

            boundaryConditions.put("POWDIFF 1.2", BoundaryCondition.powedDiff(1.2));
            boundaryConditions.put("POWDIFF 1.5", BoundaryCondition.powedDiff(1.5));
            boundaryConditions.put("POWDIFF 1.7", BoundaryCondition.powedDiff(1.7));
            boundaryConditions.put("POWDIFF 2", BoundaryCondition.powedDiff(2));

            boundaryConditions.put("CIRCLE 1.7", BoundaryCondition.circle(1.7f));
            boundaryConditions.put("CIRCLE 2", BoundaryCondition.circle(2));
            boundaryConditions.put("CIRCLE 2.3", BoundaryCondition.circle(2.3f));

            jComboBoxBoundaryConditions.removeAllItems();
            boundaryConditions.forEach((k, v)-> jComboBoxBoundaryConditions.addItem(k));
            jComboBoxBoundaryConditions.setSelectedIndex(0);
      }
      
      private void buildColorings() {
            colorings.put("HUE10", Coloring::hue10);
            colorings.put("HUE", Coloring::hue);
            colorings.put("GRAYSCALE", Coloring::grayscale);
            colorings.put("MODULO HUE", Coloring::moduloHue);
            colorings.put("MODULO GRAYSCALE", Coloring::moduloGrayscale);

            jComboBoxColoring.removeAllItems();
            colorings.forEach( (k, v)-> jComboBoxColoring.addItem(k));
            jComboBoxColoring.setSelectedIndex(0);
      }

      private void lazyGo() {
            Threads.lazy(this::guardedGo).start();
      }     
      private void guardedGo() {
            if (currentRun) {
                  nextRun = true;
                  return;
            }
            currentRun = true;
            go();
            currentRun = false;
            if (nextRun) {
                  nextRun = false;
                  Threads.lazy(this::guardedGo).start();
            }
      }
      private void go() {
            if (!initiated) return;
            renderer.setBitmapSize(jPanel1.getSize());
            int iter = jSliderIterationDepth.getValue();
            renderer.setIterationDepth(iter);

            Function<Integer, Coloring> coloringfunction = colorings.get(jComboBoxColoring.getSelectedItem());
            renderer.setColoring(coloringfunction != null ? coloringfunction.apply(iter) : Coloring.plain16());

            Equation equation = functions.get(jComboBoxFunctions.getSelectedItem());
            renderer.setEquation(equation != null ? equation : Equation.FLAT);

            BoundaryCondition boundary = boundaryConditions.get(jComboBoxBoundaryConditions.getSelectedItem());
            BoundaryCondition bc = boundary != null ? boundary : BoundaryCondition.FALSE;
            renderer.setBoundaryCondition(bc);
            renderer.setMultithreading(jCheckBoxMultithread.isSelected());
            renderer.render();
            ((ImagePanel)jPanel1).setImage(renderer.getFrontBuffer());
            jLabelCurrentPosition.setText(getCurrentPosText());
            jPanel1.repaint();
            jLabelCurrentPosition.repaint();
            showProgress(0, 0);
      }

      private double arg() {
            return jSliderParam.getValue()/1000f;
      }

      private String getCurrentPosText() {
            return "X: " + renderer.getViewport().coords.x
                    + ", Y: " + renderer.getViewport().coords.y
                    + ", Z: " + renderer.getViewport().zoom.dpp
                    + ", W: " + renderer.getGraphInfo().widthValues()
                    + ", H: " + renderer.getGraphInfo().heightValues();
                    
      }

      //<editor-fold desc="gc">
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0));
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0));
        jLabelParam = new javax.swing.JLabel();
        jPanel1 = new ImagePanel();
        jButtonRecalc = new javax.swing.JButton();
        jSliderParam = new javax.swing.JSlider();
        filler6 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 400), new java.awt.Dimension(0, 400), new java.awt.Dimension(32767, 400));
        jComboBoxFunctions = new javax.swing.JComboBox<>();
        filler3 = new javax.swing.Box.Filler(new java.awt.Dimension(80, 0), new java.awt.Dimension(80, 0), new java.awt.Dimension(80, 32767));
        jSliderIterationDepth = new javax.swing.JSlider();
        jLabelIterationDepth = new javax.swing.JLabel();
        jComboBoxBoundaryConditions = new javax.swing.JComboBox<>();
        filler7 = new javax.swing.Box.Filler(new java.awt.Dimension(400, 0), new java.awt.Dimension(400, 0), new java.awt.Dimension(400, 32767));
        jPanel2 = new javax.swing.JPanel();
        jButtonLeft = new javax.swing.JButton();
        jButtonRight = new javax.swing.JButton();
        jButtonUp = new javax.swing.JButton();
        jButtonDown = new javax.swing.JButton();
        jButtonIn = new javax.swing.JButton();
        jButtonOut = new javax.swing.JButton();
        jButtonMassiveOut = new javax.swing.JButton();
        jButtonMassiveIn = new javax.swing.JButton();
        jButtonCenter = new javax.swing.JButton();
        jButtonResetZoom = new javax.swing.JButton();
        jLabelCurrentPosition = new javax.swing.JLabel();
        jProgressBar1 = new javax.swing.JProgressBar();
        jButton1 = new javax.swing.JButton();
        jComboBoxColoring = new javax.swing.JComboBox<>();
        jCheckBoxMultithread = new javax.swing.JCheckBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                formKeyReleased(evt);
            }
        });
        getContentPane().setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        getContentPane().add(filler1, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        getContentPane().add(filler2, gridBagConstraints);

        jLabelParam.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabelParam.setText("ARG");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        getContentPane().add(jLabelParam, gridBagConstraints);

        jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        getContentPane().add(jPanel1, gridBagConstraints);

        jButtonRecalc.setText("recalc");
        jButtonRecalc.setFocusable(false);
        jButtonRecalc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonRecalcActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
        getContentPane().add(jButtonRecalc, gridBagConstraints);

        jSliderParam.setMaximum(3000);
        jSliderParam.setMinimum(1);
        jSliderParam.setFocusable(false);
        jSliderParam.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSliderParamStateChanged(evt);
            }
        });
        jSliderParam.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jSliderParamMouseReleased(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 1.0;
        getContentPane().add(jSliderParam, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        getContentPane().add(filler6, gridBagConstraints);

        jComboBoxFunctions.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jComboBoxFunctions.setFocusable(false);
        jComboBoxFunctions.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBoxFunctionsItemStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
        getContentPane().add(jComboBoxFunctions, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
        getContentPane().add(filler3, gridBagConstraints);

        jSliderIterationDepth.setMaximum(1000);
        jSliderIterationDepth.setMinimum(5);
        jSliderIterationDepth.setFocusable(false);
        jSliderIterationDepth.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSliderIterationDepthStateChanged(evt);
            }
        });
        jSliderIterationDepth.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jSliderIterationDepthMouseReleased(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        getContentPane().add(jSliderIterationDepth, gridBagConstraints);

        jLabelIterationDepth.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabelIterationDepth.setText("ITER");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        getContentPane().add(jLabelIterationDepth, gridBagConstraints);

        jComboBoxBoundaryConditions.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jComboBoxBoundaryConditions.setFocusable(false);
        jComboBoxBoundaryConditions.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBoxBoundaryConditionsItemStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
        getContentPane().add(jComboBoxBoundaryConditions, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        getContentPane().add(filler7, gridBagConstraints);

        jPanel2.setLayout(new java.awt.GridBagLayout());

        jButtonLeft.setText("<");
        jButtonLeft.setFocusable(false);
        jButtonLeft.setMaximumSize(new java.awt.Dimension(43, 23));
        jButtonLeft.setMinimumSize(new java.awt.Dimension(43, 23));
        jButtonLeft.setPreferredSize(new java.awt.Dimension(53, 23));
        jButtonLeft.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonLeftActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        jPanel2.add(jButtonLeft, gridBagConstraints);

        jButtonRight.setText(">");
        jButtonRight.setFocusable(false);
        jButtonRight.setMaximumSize(new java.awt.Dimension(43, 23));
        jButtonRight.setMinimumSize(new java.awt.Dimension(43, 23));
        jButtonRight.setPreferredSize(new java.awt.Dimension(53, 23));
        jButtonRight.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonRightActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        jPanel2.add(jButtonRight, gridBagConstraints);

        jButtonUp.setText("/\\");
            jButtonUp.setFocusable(false);
            jButtonUp.setMaximumSize(new java.awt.Dimension(43, 23));
            jButtonUp.setMinimumSize(new java.awt.Dimension(43, 23));
            jButtonUp.setPreferredSize(new java.awt.Dimension(53, 23));
            jButtonUp.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    jButtonUpActionPerformed(evt);
                }
            });
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 2;
            gridBagConstraints.gridy = 0;
            gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
            jPanel2.add(jButtonUp, gridBagConstraints);

            jButtonDown.setText("\\/");
            jButtonDown.setToolTipText("");
            jButtonDown.setFocusable(false);
            jButtonDown.setPreferredSize(new java.awt.Dimension(53, 23));
            jButtonDown.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    jButtonDownActionPerformed(evt);
                }
            });
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 3;
            gridBagConstraints.gridy = 0;
            gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
            jPanel2.add(jButtonDown, gridBagConstraints);

            jButtonIn.setText("+");
            jButtonIn.setFocusable(false);
            jButtonIn.setMaximumSize(new java.awt.Dimension(43, 23));
            jButtonIn.setMinimumSize(new java.awt.Dimension(43, 23));
            jButtonIn.setPreferredSize(new java.awt.Dimension(53, 23));
            jButtonIn.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    jButtonInActionPerformed(evt);
                }
            });
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 7;
            gridBagConstraints.gridy = 0;
            gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
            jPanel2.add(jButtonIn, gridBagConstraints);

            jButtonOut.setText("-");
            jButtonOut.setFocusable(false);
            jButtonOut.setMaximumSize(new java.awt.Dimension(43, 23));
            jButtonOut.setMinimumSize(new java.awt.Dimension(43, 23));
            jButtonOut.setPreferredSize(new java.awt.Dimension(53, 23));
            jButtonOut.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    jButtonOutActionPerformed(evt);
                }
            });
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 8;
            gridBagConstraints.gridy = 0;
            gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
            jPanel2.add(jButtonOut, gridBagConstraints);

            jButtonMassiveOut.setText("--");
            jButtonMassiveOut.setFocusable(false);
            jButtonMassiveOut.setMaximumSize(new java.awt.Dimension(43, 23));
            jButtonMassiveOut.setMinimumSize(new java.awt.Dimension(43, 23));
            jButtonMassiveOut.setPreferredSize(new java.awt.Dimension(53, 23));
            jButtonMassiveOut.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    jButtonMassiveOutActionPerformed(evt);
                }
            });
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 9;
            gridBagConstraints.gridy = 0;
            gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
            jPanel2.add(jButtonMassiveOut, gridBagConstraints);

            jButtonMassiveIn.setText("++");
            jButtonMassiveIn.setFocusable(false);
            jButtonMassiveIn.setMaximumSize(new java.awt.Dimension(43, 23));
            jButtonMassiveIn.setMinimumSize(new java.awt.Dimension(43, 23));
            jButtonMassiveIn.setPreferredSize(new java.awt.Dimension(53, 23));
            jButtonMassiveIn.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    jButtonMassiveInActionPerformed(evt);
                }
            });
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 6;
            gridBagConstraints.gridy = 0;
            gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
            jPanel2.add(jButtonMassiveIn, gridBagConstraints);

            jButtonCenter.setText("C");
            jButtonCenter.setFocusable(false);
            jButtonCenter.setPreferredSize(new java.awt.Dimension(53, 23));
            jButtonCenter.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    jButtonCenterActionPerformed(evt);
                }
            });
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 4;
            gridBagConstraints.gridy = 0;
            gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
            jPanel2.add(jButtonCenter, gridBagConstraints);

            jButtonResetZoom.setText("R");
            jButtonResetZoom.setFocusable(false);
            jButtonResetZoom.setPreferredSize(new java.awt.Dimension(53, 23));
            jButtonResetZoom.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    jButtonResetZoomActionPerformed(evt);
                }
            });
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 5;
            gridBagConstraints.gridy = 0;
            gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
            gridBagConstraints.weightx = 1.0;
            jPanel2.add(jButtonResetZoom, gridBagConstraints);

            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 1;
            gridBagConstraints.gridy = 3;
            gridBagConstraints.gridwidth = 4;
            gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
            gridBagConstraints.weightx = 1.0;
            gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
            getContentPane().add(jPanel2, gridBagConstraints);

            jLabelCurrentPosition.setText("0, 0, 0");
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 1;
            gridBagConstraints.gridy = 5;
            gridBagConstraints.gridwidth = 3;
            gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
            gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 5);
            getContentPane().add(jLabelCurrentPosition, gridBagConstraints);
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 4;
            gridBagConstraints.gridy = 5;
            gridBagConstraints.gridwidth = 2;
            gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
            gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
            getContentPane().add(jProgressBar1, gridBagConstraints);

            jButton1.setText("defocus");
            jButton1.setFocusable(false);
            jButton1.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    jButton1ActionPerformed(evt);
                }
            });
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 5;
            gridBagConstraints.gridy = 3;
            gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
            getContentPane().add(jButton1, gridBagConstraints);

            jComboBoxColoring.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
            jComboBoxColoring.setFocusable(false);
            jComboBoxColoring.addItemListener(new java.awt.event.ItemListener() {
                public void itemStateChanged(java.awt.event.ItemEvent evt) {
                    jComboBoxColoringItemStateChanged(evt);
                }
            });
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 4;
            gridBagConstraints.gridy = 1;
            gridBagConstraints.gridwidth = 2;
            gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 5);
            getContentPane().add(jComboBoxColoring, gridBagConstraints);

            jCheckBoxMultithread.setText("multithread");
            jCheckBoxMultithread.setFocusable(false);
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 3;
            gridBagConstraints.gridy = 1;
            gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
            gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
            getContentPane().add(jCheckBoxMultithread, gridBagConstraints);

            pack();
        }// </editor-fold>//GEN-END:initComponents

      private void jButtonRecalcActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonRecalcActionPerformed
            lazyGo();
      }//GEN-LAST:event_jButtonRecalcActionPerformed

      private void jSliderParamStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSliderParamStateChanged
            jLabelParam.setText("P: " + (float)arg());
      }//GEN-LAST:event_jSliderParamStateChanged

      private void jComboBoxFunctionsItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBoxFunctionsItemStateChanged
            lazyGo();
      }//GEN-LAST:event_jComboBoxFunctionsItemStateChanged

      private void jSliderIterationDepthStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSliderIterationDepthStateChanged
            jLabelIterationDepth.setText("DEPTH: " + jSliderIterationDepth.getValue());
      }//GEN-LAST:event_jSliderIterationDepthStateChanged

      private void jComboBoxBoundaryConditionsItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBoxBoundaryConditionsItemStateChanged
            lazyGo();
      }//GEN-LAST:event_jComboBoxBoundaryConditionsItemStateChanged

      private void jButtonLeftActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonLeftActionPerformed
            camera.left();
            lazyGo();
      }//GEN-LAST:event_jButtonLeftActionPerformed

      private void jButtonRightActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonRightActionPerformed
            camera.right();
            lazyGo();
      }//GEN-LAST:event_jButtonRightActionPerformed

      private void jButtonUpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonUpActionPerformed
            camera.up();
            lazyGo();
      }//GEN-LAST:event_jButtonUpActionPerformed

      private void jButtonDownActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDownActionPerformed
            camera.down();
            lazyGo();
      }//GEN-LAST:event_jButtonDownActionPerformed

      private void jButtonInActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonInActionPerformed
            camera.in();
            lazyGo();
      }//GEN-LAST:event_jButtonInActionPerformed

      private void jButtonOutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonOutActionPerformed
            camera.out();
            lazyGo();
      }//GEN-LAST:event_jButtonOutActionPerformed

      private void jButtonMassiveInActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonMassiveInActionPerformed
            camera.massiveIn();
            lazyGo();
      }//GEN-LAST:event_jButtonMassiveInActionPerformed

      private void jButtonMassiveOutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonMassiveOutActionPerformed
            camera.massiveOut();
            lazyGo();
      }//GEN-LAST:event_jButtonMassiveOutActionPerformed

      private void jButtonCenterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCenterActionPerformed
            camera.centerAtZeroZero();
            lazyGo();
      }//GEN-LAST:event_jButtonCenterActionPerformed

      private void jButtonResetZoomActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonResetZoomActionPerformed
            camera.resetZoom();
            lazyGo();
      }//GEN-LAST:event_jButtonResetZoomActionPerformed

      private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
      }//GEN-LAST:event_jButton1ActionPerformed

      private void formKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_formKeyReleased
            Runnable r = keyRelease(evt);
            evt.consume();
            syncRun(r);
      }//GEN-LAST:event_formKeyReleased

      private void jSliderParamMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jSliderParamMouseReleased
            jLabelParam.setText("P: " + (float)arg());
            lazyGo();
      }//GEN-LAST:event_jSliderParamMouseReleased

      private void jSliderIterationDepthMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jSliderIterationDepthMouseReleased
            jLabelIterationDepth.setText("DEPTH: " + jSliderIterationDepth.getValue());
            lazyGo();
      }//GEN-LAST:event_jSliderIterationDepthMouseReleased

      private void jComboBoxColoringItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBoxColoringItemStateChanged
            lazyGo();
      }//GEN-LAST:event_jComboBoxColoringItemStateChanged
      
      private volatile boolean running = false; 
      private void syncRun(Runnable r) {
            if (running) return;
            running = true;
            r.run();
            running = false;
      } 

      //</editor-fold>
      
      //<editor-fold desc="var decl">
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler2;
    private javax.swing.Box.Filler filler3;
    private javax.swing.Box.Filler filler6;
    private javax.swing.Box.Filler filler7;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButtonCenter;
    private javax.swing.JButton jButtonDown;
    private javax.swing.JButton jButtonIn;
    private javax.swing.JButton jButtonLeft;
    private javax.swing.JButton jButtonMassiveIn;
    private javax.swing.JButton jButtonMassiveOut;
    private javax.swing.JButton jButtonOut;
    private javax.swing.JButton jButtonRecalc;
    private javax.swing.JButton jButtonResetZoom;
    private javax.swing.JButton jButtonRight;
    private javax.swing.JButton jButtonUp;
    private javax.swing.JCheckBox jCheckBoxMultithread;
    private javax.swing.JComboBox<String> jComboBoxBoundaryConditions;
    private javax.swing.JComboBox<String> jComboBoxColoring;
    private javax.swing.JComboBox<String> jComboBoxFunctions;
    private javax.swing.JLabel jLabelCurrentPosition;
    private javax.swing.JLabel jLabelIterationDepth;
    private javax.swing.JLabel jLabelParam;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JProgressBar jProgressBar1;
    private javax.swing.JSlider jSliderIterationDepth;
    private javax.swing.JSlider jSliderParam;
    // End of variables declaration//GEN-END:variables
      //</editor-fold>
      
      //---------------------------------------------------------------------------------------------------
      private void showProgress(int val, int max) {
            progressThrottler.run(()-> rawShowProgress(val, max));
      }
       
      private void rawShowProgress(int val, int max) {
            int nval = 0;
            if (max != 0) 
                  nval = 100*val/max;
            if (nval != jProgressBar1.getValue()) {
                  jProgressBar1.setValue(nval);
                  jProgressBar1.repaint();
           }
                  
      }

      private Runnable keyRelease(KeyEvent evt) {
            Runnable action = keyReleaseAction(evt);
            if (action == null) 
                  return ()->{};
            
            int iterations = 1;
            if (evt.isControlDown())
                  iterations *= 10;
            if (evt.isShiftDown())
                  iterations *= 100;

            int finaliterations = iterations;
            return ()-> {
                 for ( int i = 0 ; i < finaliterations ; ++i )
                 action.run();
                 
                 lazyGo();
            };
            
      }
      private Runnable keyReleaseAction(KeyEvent evt) {
            switch (evt.getKeyCode()) {
                  case KeyEvent.VK_LEFT: return camera::left;
                  case KeyEvent.VK_RIGHT: return camera::right;
                  case KeyEvent.VK_UP: return camera::up;
                  case KeyEvent.VK_DOWN: return camera::down;
                  case KeyEvent.VK_C: return camera::centerAtZeroZero;
                  case KeyEvent.VK_R: return camera::resetZoom;
                  case KeyEvent.VK_EQUALS: return camera::in;
                  case KeyEvent.VK_MINUS: return camera::out;
                  case KeyEvent.VK_COMMA: return ()-> jSliderIterationDepth.setValue(jSliderIterationDepth.getValue()-1);
                  case KeyEvent.VK_PERIOD: return ()-> jSliderIterationDepth.setValue(jSliderIterationDepth.getValue()+1);
                  case KeyEvent.VK_OPEN_BRACKET: return ()-> jSliderParam.setValue(jSliderParam.getValue()-1);
                  case KeyEvent.VK_CLOSE_BRACKET: return ()-> jSliderParam.setValue(jSliderParam.getValue()+1);
                  
                  default: return null;
            }
      }
                                
      
}
