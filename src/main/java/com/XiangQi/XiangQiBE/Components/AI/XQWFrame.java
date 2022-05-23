package com.XiangQi.XiangQiBE.Components.AI;

import java.awt.Button;
import java.awt.Canvas;
import java.awt.Checkbox;
import java.awt.Choice;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Label;
import java.awt.List;
import java.awt.Scrollbar;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.ImageIcon;

public class XQWFrame extends javax.swing.JFrame {
    private static final long serialVersionUID = 1L;

    private static final int RESP_CLICK = 0;
    private static final int RESP_ILLEGAL = 1;
    private static final int RESP_MOVE = 2;
    private static final int RESP_MOVE2 = 3;
    private static final int RESP_CAPTURE = 4;
    private static final int RESP_CAPTURE2 = 5;
    private static final int RESP_CHECK = 6;
    private static final int RESP_CHECK2 = 7;
    private static final int RESP_WIN = 8;
    private static final int RESP_DRAW = 9;
    private static final int RESP_LOSS = 10;

    private static final int PIECE_MARGIN = 8;
    private static final int SQUARE_SIZE = 56;
    private static final int BOARD_WIDTH = 521;
    private static final int BOARD_HEIGHT = 577;
    private static final int ITEM_WIDTH = 100;
    private static final int ITEM_HEIGHT = 20;
    
    private static final String[] PIECE_NAME = {
        null, null, null, null, null, null, null, null,
        "rk", "ra", "rb", "rn", "rr", "rc", "rp", null,
        "bk", "ba", "bb", "bn", "br", "bc", "bp", null,
    };

    private static final String[] BOARD_NAME = {
        "wood", "green", "white", "sheet", "canvas", "drops", "qianhong"
    };

    private static final String[] PIECES_NAME = {
        "wood", "delicate", "polish"
    };

    private static final String[] SOUND_NAME = {
        "click", "illegal", "move", "move2", "capture", "capture2",
        "check", "check2", "win", "draw", "loss"
    };

    private static final String[] MUSIC_NAME = {
        "express", "funny", "classic", "mozart1", "mozart4", "furelise",
        "lovdream", "waltz", "humour", "pal", "cmusic"
    };

    static final int MUSIC_MUTE = MUSIC_NAME.length;

    //"入门", "业余", "专业", "大师", "特级大师"
    static final String[] LEVEL_TEXT = {
        "Beginner", "Amateur", "Profession", "Master", "Grand Master"
    };

    Image[] imgPieces = new Image[PIECE_NAME.length];
    Image imgSelected, imgBoard;
    //AudioClip[] acSounds = new AudioClip[SOUND_NAME.length];
    //AudioClip acMusic;

    Canvas canvas = new Canvas() {
        private static final long serialVersionUID = 1L;

        @Override
        public void paint(Graphics g) {
            g.drawImage(imgBoard, 0, 0, this);
            for (int x = Position.FILE_LEFT; x <= Position.FILE_RIGHT; x ++) {
                for (int y = Position.RANK_TOP; y <= Position.RANK_BOTTOM; y ++) {
                    int sq = Position.COORD_XY(x, y);
                    sq = (flipped ? Position.SQUARE_FLIP(sq) : sq);
                    int xx = PIECE_MARGIN + (x - Position.FILE_LEFT) * SQUARE_SIZE;
                    int yy = PIECE_MARGIN + (y - Position.RANK_TOP) * SQUARE_SIZE;
                    int pc = pos.squares[sq];
                    if (pc > 0) {
                        g.drawImage(imgPieces[pc], xx, yy, this);
                    }
                    if (sq == sqSelected || sq == Position.SRC(mvLast) || sq == Position.DST(mvLast)) {
                        g.drawImage(imgSelected, xx, yy, this);
                    }
                }
            }
        }
    };
    Button btnMessage = new Button();

    Position pos = new Position();
    Search search = new Search(pos, 16);
    String currentFen = Position.STARTUP_FEN[0], retractFen;
    int sqSelected, mvLast;

    volatile boolean thinking = false;
    boolean flipped = false, sound = true;
    int handicap = 0, level = 0, board = 0, pieces = 0, music = 8;
    
    public XQWFrame() {
        initComponents();
        //setLayout(null);
        setExtendedState(MAXIMIZED_BOTH);
        btnMessage.addActionListener((ActionEvent e) -> {
            currentFen = Position.STARTUP_FEN[handicap];
            restart();
            canvas.repaint();
        });
        btnMessage.setBounds(BOARD_WIDTH / 4, (BOARD_HEIGHT - ITEM_HEIGHT) / 2, BOARD_WIDTH / 2, ITEM_HEIGHT);
        btnMessage.setVisible(false);
        add(btnMessage);

        canvas.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                    // Do Nothing
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                    // Do Nothing
            }

            @Override
            public void mouseExited(MouseEvent e) {
                    // Do Nothing
            }

            @Override
            public void mousePressed(MouseEvent e) {
                    if (!thinking && !btnMessage.isVisible()) {
                            int x = Util.MIN_MAX(0, (e.getX() - PIECE_MARGIN) / SQUARE_SIZE, 8);
                            int y = Util.MIN_MAX(0, (e.getY() - PIECE_MARGIN) / SQUARE_SIZE, 9);
                            clickSquare(Position.COORD_XY(x + Position.FILE_LEFT, y + Position.RANK_TOP));
                    }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                    // Do Nothing
            }
        });
        canvas.setBounds(0, 0, BOARD_WIDTH, BOARD_HEIGHT);
        add(canvas);
        
        //谁先走：
        addItem("Who goes first:", 0);
        final List optFlipped = new List(2);
        optFlipped.add("I go first"); //我先走
        optFlipped.add("Computer go first"); //电脑先走
        optFlipped.select(flipped ? 1 : 0);
        addItem(optFlipped, 1, 2);

        //先走让子：
        addItem("Handicap:", 3);
        final List optHandicap = new List(4);
        optHandicap.add("None"); //不让子
        optHandicap.add("Left Horse"); //让左马
        optHandicap.add("Both Horses"); //让双马
        optHandicap.add("All Soldiers, Elephants, Advisors"); //让九子
        optHandicap.select(handicap);
        addItem(optHandicap, 4, 4);

        //重新开始
        Button btnRestart = new Button("Restart");
        btnRestart.addActionListener((ActionEvent e) -> {
            if (!thinking) {
                flipped = (optFlipped.getSelectedIndex() == 1);
                handicap = optHandicap.getSelectedIndex();
                currentFen = Position.STARTUP_FEN[handicap];
                restart();
                canvas.repaint();
            }
        });
        addItem(btnRestart, 8);

        //悔棋
        Button btnRetract = new Button("Undo");
        btnRetract.addActionListener((ActionEvent e) -> {
            if (!thinking) {
                currentFen = retractFen;
                restart();
                canvas.repaint();
            }
        });
        addItem(btnRetract, 9);

        //级别：
        final Label lblLevel = new Label("Level: " + LEVEL_TEXT[level]);
        addItem(lblLevel, 10);
        final Scrollbar sbLevel = new Scrollbar(Scrollbar.HORIZONTAL, level, 1, 0, 5);
        sbLevel.addAdjustmentListener((AdjustmentEvent e) -> {
            level = sbLevel.getValue();
            lblLevel.setText("Level: " + LEVEL_TEXT[level]); //级别：
        });
        addItem(sbLevel, 11);

        //棋盘：
        addItem("Chessboard:", -7);
        final Choice optBoard = new Choice();
        optBoard.add("Oak"); //栎木
        optBoard.add("Green marble"); //绿色大理石
        optBoard.add("White marble"); //白色大理石
        optBoard.add("Paper"); //再生纸
        optBoard.add("Canvas"); //画布
        optBoard.add("Water droplets"); //水滴
        optBoard.add("Light red ?"); //浅红象棋
        optBoard.select(board);
        optBoard.addItemListener((ItemEvent e) -> {
            board = optBoard.getSelectedIndex();
            loadBoard();
            canvas.repaint();
        });
        addItem(optBoard, -6);

        //棋子：
        addItem("Pieces", -5);
        final Choice optPieces = new Choice();
        optPieces.add("Wood"); //木刻
        optPieces.add("Delicate"); //精致
        optPieces.add("Polish"); //光泽
        optPieces.select(pieces);
        optPieces.addItemListener((ItemEvent e) -> {
            pieces = optPieces.getSelectedIndex();
            loadPieces();
            canvas.repaint();
        });
        addItem(optPieces, -4);

        //音效
//        final Checkbox chkMute = new Checkbox("Sound effect", sound);
//        chkMute.addItemListener((ItemEvent e) -> {
//            sound = chkMute.getState();
//            playSound(0);
//        });
//        addItem(chkMute, -3);

        //音乐
//        addItem("Music", -2);
//        final Choice optMusic = new Choice();
//        optMusic.add("紧张");
//        optMusic.add("风趣");
//        optMusic.add("巴赫");
//        optMusic.add("莫扎特一");
//        optMusic.add("莫扎特四");
//        optMusic.add("贝多芬");
//        optMusic.add("李斯特");
//        optMusic.add("柴可夫斯基");
//        optMusic.add("德沃夏克");
//        optMusic.add("仙剑奇侠传");
//        optMusic.add("望江南");
//        optMusic.add("无声");
//        optMusic.select(music);
//        optMusic.addItemListener((ItemEvent e) -> {
//            music = optMusic.getSelectedIndex();
//            stopMusic();
//            startMusic();
//        });
//        addItem(optMusic, -1);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 721, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 376, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

	public void init() {
            loadBoard();
            loadPieces();
            //for (int i = 0; i < SOUND_NAME.length; i ++) {
            //	acSounds[i] = getAudioClip(getCodeBase(), "sounds/" + SOUND_NAME[i] + ".wav");
            //}
            //startMusic();
            restart();
	}

	public void destroy() {
            stopMusic();
	}

	void addItem(String label, int top) {
            addItem(new Label(label), top);
	}

	void addItem(Component component, int top) {
            addItem(component, top, 1);
	}

	void addItem(Component component, int top, int height) {
            if (top < 0) {
                component.setBounds(BOARD_WIDTH, BOARD_HEIGHT + ITEM_HEIGHT * top, ITEM_WIDTH, ITEM_HEIGHT * height);
            } else {
                component.setBounds(BOARD_WIDTH, ITEM_HEIGHT * top, ITEM_WIDTH, ITEM_HEIGHT * height);
            }
            add(component);
	}

	void loadBoard() {
            imgBoard = new ImageIcon(this.getClass().getResource("/boards/" + BOARD_NAME[board] + ".gif")).getImage();
	}

	void loadPieces() {
            for (int i = 0; i < PIECE_NAME.length; i ++) {
                imgPieces[i] = (PIECE_NAME[i] == null ? null : new ImageIcon(this.getClass().getResource("/pieces/" + PIECES_NAME[pieces] + "/" + PIECE_NAME[i] + ".gif")).getImage());
            }
            imgSelected = new ImageIcon(this.getClass().getResource("/pieces/" + PIECES_NAME[pieces] + "/oos.gif")).getImage();
	}

	void startMusic() {
		//if (music == MUSIC_MUTE) {
//			acMusic = null;
//		} else {
//			acMusic = getAudioClip(getCodeBase(), "musics/" + MUSIC_NAME[music] + ".mid");
//			if (acMusic != null) {
//				acMusic.loop();
//			}
//		}
	}

	void stopMusic() {
//		if (acMusic != null) {
//			acMusic.stop();
//		}
	}

	void restart() {
            btnMessage.setVisible(false);
            pos.fromFen(currentFen);
            retractFen = currentFen;
            sqSelected = mvLast = 0;
            if (flipped && pos.sdPlayer == 0) {
                thinking();
            }
	}

	void clickSquare(int sq_) {
            int sq = (flipped ? Position.SQUARE_FLIP(sq_) : sq_);
            int pc = pos.squares[sq];
            if ((pc & Position.SIDE_TAG(pos.sdPlayer)) != 0) {
                if (sqSelected > 0) {
                    drawSquare(sqSelected);
                }
                if (mvLast > 0) {
                    drawMove(mvLast);
                    mvLast = 0;
                }
                sqSelected = sq;
                drawSquare(sq);
                playSound(RESP_CLICK);
            } else if (sqSelected > 0) {
                int mv = Position.MOVE(sqSelected, sq);
                if (!pos.legalMove(mv)) {
                    return;
                }
                if (!pos.makeMove(mv)) {
                    playSound(RESP_ILLEGAL);
                    return;
                }
                int response = pos.inCheck() ? RESP_CHECK :
                    pos.captured() ? RESP_CAPTURE : RESP_MOVE;
                if (pos.captured()) {
                    pos.setIrrev();
                }
                mvLast = mv;
                sqSelected = 0;
                drawMove(mv);
                playSound(response);
                if (!getResult()) {
                    thinking();
                }
            }
	}

	void drawSquare(int sq_) {
            int sq = (flipped ? Position.SQUARE_FLIP(sq_) : sq_);
            int x = PIECE_MARGIN + (Position.FILE_X(sq) - Position.FILE_LEFT) * SQUARE_SIZE;
            int y = PIECE_MARGIN + (Position.RANK_Y(sq) - Position.RANK_TOP) * SQUARE_SIZE;
            canvas.repaint(x, y, SQUARE_SIZE, SQUARE_SIZE);
	}

	void drawMove(int mv) {
            drawSquare(Position.SRC(mv));
            drawSquare(Position.DST(mv));
	}

	void playSound(int response) {
//		if (sound && acSounds[response] != null) {
//			acSounds[response].play();
//		}
	}

	void showMessage(String message) {
            btnMessage.setLabel(message);
            btnMessage.setVisible(true);
	}

	void thinking() {
            thinking = true;
            new Thread() {
                @Override
                public void run() {
                    setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                    int mv = mvLast;
                    mvLast = search.searchMain(100 << (level << 1));
                    pos.makeMove(mvLast);
                    drawMove(mv);
                    drawMove(mvLast);
                    int response = pos.inCheck() ? RESP_CHECK2 : pos.captured() ? RESP_CAPTURE2 : RESP_MOVE2;
                    if (pos.captured()) {
                        pos.setIrrev();
                    }
                    setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                    getResult(response);
                    thinking = false;
                }
            }.start();
	}

	/** Player Move Result */
	boolean getResult() {
            return getResult(-1);
	}

	/** Computer Move Result */
	boolean getResult(int response) {
            if (pos.isMate()) {
                playSound(response < 0 ? RESP_WIN : RESP_LOSS);
                //"祝贺你取得胜利！" : "请再接再厉！"
                showMessage(response < 0 ? "Congratulations on your victory!" : "Keep up the good work!");
                return true;
            }
            int vlRep = pos.repStatus(3);
            if (vlRep > 0) {
                vlRep = (response < 0 ? pos.repValue(vlRep) : -pos.repValue(vlRep));
                playSound(vlRep > Position.WIN_VALUE ? RESP_LOSS : vlRep < -Position.WIN_VALUE ? RESP_WIN : RESP_DRAW);
                //长打作负，请不要气馁！
                showMessage(vlRep > Position.WIN_VALUE ? "Don't be discouraged!" : 
                        //"电脑长打作负，祝贺你取得胜利！" : "双方不变作和，辛苦了！"
                        vlRep < -Position.WIN_VALUE ? "The computer has lost, congratulations on your victory!" : "The two sides do not make peace, hard work!");
                return true;
            }
            if (pos.moveNum > 100) {
                playSound(RESP_DRAW);
                showMessage("Draw!"); //超过自然限着作和，辛苦了！
                return true;
            }
            if (response >= 0) {
                playSound(response);
                retractFen = currentFen;
                currentFen = pos.toFen();
            }
            return false;
	}

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> {
            new XQWFrame().setVisible(true);
        });
    }
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
