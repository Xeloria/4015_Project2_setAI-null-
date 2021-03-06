package rbadia.voidspace.main;

import java.awt.event.KeyEvent;


import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import javax.swing.JOptionPane;

import rbadia.voidspace.model.Floor;
import rbadia.voidspace.model.MegaMan;

/**
 * Handles user input events.
 */
public class InputHandler implements KeyListener{
	private boolean leftIsPressed;
	private boolean rightIsPressed;
	private boolean downIsPressed;
	private boolean jumpIsPressed;
	private boolean spaceIsPressed = false;
	private boolean shiftIsPressed;
	private boolean eIsPressed;
	private boolean qIsPressed;
	private boolean hIsPressed;
	private boolean nIsPressed = false;
	private boolean gIsPressed;
	private boolean aimUpIsPressed;
	
	private boolean fIsPressed = false;
	private boolean fireRate = false;

	private long lastBulletTime;
	private long lastExchangeTime;
	private long lastMessageTime;
	private long lastBigBulletTime;
	private long lastSkipTime;
	private int stack= 0;
	private int mute = 0;


	private GameLogic gameLogic;
	private GameScreen gScreen;
	private int maxLevels=2;


	/**
	 * Create a new input handlera
	 * @param gameLogic the game logic handler
	 */
	public InputHandler(GameLogic gameLogic){
		this.gameLogic = gameLogic;
	}

	/**
	 * Handle user input after screen update.
	 * @param gameScreen he game screen
	 */
	public void handleInput(GameScreen gameScreen){
		GameStatus status = gameLogic.getStatus();

		if(!status.isGameOver() && !status.isNewMegaMan() && !status.isGameStarting() && !status.isGameWon() && !status.isLevelWon()){
			// fire bullet if space is pressed
			if(spaceIsPressed){
				if(!fireRate){
					// fire only up to 5 bullets per second
					long currentTime = System.currentTimeMillis();
					if((currentTime - lastBulletTime) > 1000/5){
						lastBulletTime = currentTime;
						gameLogic.fireBullet();
					}
				} else{
					//Ridiculous fire rate
					gameLogic.fireBullet();
				}
			}

			if(eIsPressed){
				if(status.getScore()>= 1500){
					long currentTime = System.currentTimeMillis();
					if((currentTime - lastExchangeTime > 1000*2)){
						lastExchangeTime = currentTime;
						status.setScore(status.getScore() - 1500);
						status.setShipsLeft(status.getShipsLeft() + 10);
						System.out.println("Health increased by 10. Price: 1500");
					}
//					else{
//						currentTime = System.currentTimeMillis();
//						if((currentTime - lastMessageTime > 1000*2)){
//							lastMessageTime = currentTime;
//							System.out.println("You must wait to perform this action.");
//						}
//					}
				}
			}

			if(qIsPressed){
				if(!status.isGameOver() && !status.isNewMegaMan() && !status.isGameStarting() && !status.isGameWon() && !status.isLevelWon()){
					if(stack==0 && status.getScore()>= 0){
						stack++;
						status.setScore(status.getScore()-50);
					}
					else if(stack>= 1){
						long currentTime = System.currentTimeMillis();
						if((currentTime - lastBigBulletTime) > 1000){
							lastBigBulletTime = currentTime;
							gameLogic.fireBigBullet();
						}

					}
				}
				else{

				}
			}

			//WIP
			//			if(mIsPressed){
			//				mute=1;
			//			}

			MegaMan megaMan = gameLogic.getMegaMan();
			Floor[] floor = gameLogic.getFloor();

			if(shiftIsPressed){
				megaMan.setSpeed(megaMan.getDefaultSpeed() * 2 +1);
			}
			if(aimUpIsPressed){
				status.setUp(true);			
				}
			if(!aimUpIsPressed){
				status.setUp(false);			
				}

			if(jumpIsPressed){
				long currentTime = System.currentTimeMillis();
				if((currentTime - lastBigBulletTime) > 570){ //if i<10 (700)
					lastBigBulletTime = currentTime;
					for(int i=0; i<6; i++){
						moveMegaManUp(megaMan);
					}
				}
			}

			if(downIsPressed){
				moveMegaManDown(megaMan, gameScreen.getHeight(), floor);
			}

			if(leftIsPressed){
				moveMegaManLeft(megaMan);
			}

			if(rightIsPressed){
				moveMegaManRight(megaMan, gameScreen.getWidth());
			}
			
			if(nIsPressed){
				if(status.isGameStarted()&&!status.isGameOver() && !status.isNewMegaMan() && !status.isGameStarting() && !status.isGameWon() && !status.isLevelWon()){
					long currentTime = System.currentTimeMillis();
						if((currentTime - lastSkipTime > 1000*10)){
							lastSkipTime = currentTime;				
							gameScreen.nextLevel(status.getLevel()+1);
							System.out.println("Skipping to the next level: "+status.getLevel());
						} else{
							currentTime = System.currentTimeMillis();
							if((currentTime - lastMessageTime > 1000*2)){
								lastMessageTime = currentTime;
								System.out.println("You must wait to perform this action.");
							}
						}
					}
				}

			//IMMORTALITY
			if(gIsPressed){
				long currentTime = System.currentTimeMillis();
				if((currentTime - lastExchangeTime > 1000*2)){
					lastExchangeTime = currentTime;
					status.setImmortal(!status.isImmortal());
					if(status.isImmortal()){
						System.out.println("You are now immortal.");
					}else{
						System.out.println("You are no longer immortal.");
					}
				}
//				else{
//					currentTime = System.currentTimeMillis();
//					if((currentTime - lastMessageTime > 1000*2)){
//						lastMessageTime = currentTime;
//						System.out.println("You must wait to perform this action.");
//					}
//				}
			}
			
			if(hIsPressed){
				
				long currentTime = System.currentTimeMillis();
				if((currentTime - lastExchangeTime > 1000*2)){
					lastExchangeTime = currentTime;
					if(status.getScore()>=150){
						status.setShipsLeft(status.getShipsLeft()+1);
						status.setScore(status.getScore()-150);
						System.out.println("Health increased by 1. Price: 150");
					}
//					else{
//						currentTime = System.currentTimeMillis();
//						if((currentTime - lastMessageTime > 1000*2)){
//							lastMessageTime = currentTime;
//							System.out.println("You must wait to perform this action.");
//						}
//					}
				}
				
			}
			
			if(fIsPressed){
				long currentTime = System.currentTimeMillis();
				if((currentTime - lastExchangeTime > 1000*2)){
					lastExchangeTime = currentTime;
					fireRate=!fireRate;
					if(fireRate){
						System.out.println("Fire rate increased.");
					}else{
						System.out.println("Fire rate decreased.");
					}
				}
//				else{
//					currentTime = System.currentTimeMillis();
//					if((currentTime - lastMessageTime > 1000*2)){
//						lastMessageTime = currentTime;
//						System.out.println("You must wait to perform this action.");
//					}
//				}
			}
			
			}
		}


	/**
	 * Move the megaMan up
	 * @param megaMan the megaMan
	 */
	private void moveMegaManUp(MegaMan megaMan){
		if(megaMan.getY() - megaMan.getSpeed() >= 0){
			megaMan.translate(0, -megaMan.getSpeed()*2);
		}
	}



	/**
	 * Move the megaMan down
	 * @param megaMan the megaMan
	 */
	private void moveMegaManDown(MegaMan megaMan, int screenHeight, Floor[] floor){
		for(int i=0; i<9; i++){
			if(megaMan.getY() + megaMan.getSpeed() + megaMan.height < screenHeight - floor[i].getFloorHeight()/2){
				megaMan.translate(0, 2);
			}
		}
	}

	/**
	 * Move the megaMan left
	 * @param megaMan the megaMan
	 */
	private void moveMegaManLeft(MegaMan megaMan){
		if(megaMan.getX() - megaMan.getSpeed() >= 0){
			megaMan.translate(-megaMan.getSpeed(), 0);
		}
	}

	/**
	 * Move the megaMan right
	 * @param megaMan the megaMan
	 */
	private void moveMegaManRight(MegaMan megaMan, int screenWidth){
		if(megaMan.getX() + megaMan.getSpeed() + megaMan.width < screenWidth){
			megaMan.translate(megaMan.getSpeed(), 0);
		}
	}
	/**
	 * Handle a key input event.
	 */
	public void keyPressed(KeyEvent e) {
		GameStatus status = gameLogic.getStatus();
		switch(e.getKeyCode()){
		case KeyEvent.VK_UP:
			if(!status.isGameStarted() && !status.isGameOver() && !status.isGameStarting() && !status.isGameWon() && !status.isLevelWon()){
			}
			else{
				this.jumpIsPressed = true;
			}
			break;
		case KeyEvent.VK_DOWN:
			if(!status.isGameStarted() && !status.isGameOver() && !status.isGameStarting() && !status.isGameWon() && !status.isLevelWon()){
			}
			else{
				this.downIsPressed = true;
			}
			break;
		case KeyEvent.VK_LEFT:
			if(!status.isGameStarted() && !status.isGameOver() && !status.isGameStarting() && !status.isGameWon() && !status.isLevelWon()){
			}
			else{
				this.leftIsPressed = true;
			}
			break;
		case KeyEvent.VK_RIGHT:
			if(!status.isGameStarted() && !status.isGameOver() && !status.isGameStarting() && !status.isGameWon() && !status.isLevelWon()){
			}
			else{
				this.rightIsPressed = true;
			}
			break;
		case KeyEvent.VK_SPACE:
			if(!status.isGameStarted() && !status.isGameOver() && !status.isGameStarting() && !status.isGameWon() && !status.isLevelWon()){
				// new game
				lastBulletTime = System.currentTimeMillis();
				leftIsPressed = false;
				rightIsPressed = false;
				downIsPressed = false;
				jumpIsPressed = false;
				spaceIsPressed = false;
				stack=0;
				gameLogic.newGame();

				//WIP
				//				if(mute==0){

				//Music
				//changes music from "menu music" to "ingame music"
				VoidSpaceMain.audioClip.close();
				VoidSpaceMain.audioFile = new File("audio/mainGame.wav");
				try {
					VoidSpaceMain.audioStream = AudioSystem.getAudioInputStream(VoidSpaceMain.audioFile);
					VoidSpaceMain.audioClip.open(VoidSpaceMain.audioStream);
					VoidSpaceMain.audioClip.start();
					VoidSpaceMain.audioClip.loop(Clip.LOOP_CONTINUOUSLY);
				} catch (UnsupportedAudioFileException e1) {
					e1.printStackTrace();
				} catch (IOException e1) {
					e1.printStackTrace();
				} catch (LineUnavailableException e1) {
					e1.printStackTrace();
				}
				//				}
			}
			else{
				this.spaceIsPressed = true;

			}
			break;
		case KeyEvent.VK_SHIFT:
			this.shiftIsPressed = true;
			break;
		case KeyEvent.VK_ESCAPE:
			System.exit(1);
			break;
		case KeyEvent.VK_E:
			if(!status.isGameStarted() && !status.isGameOver() && !status.isGameStarting() && !status.isGameWon() && !status.isLevelWon()){
			}
			else if(status.getScore() < 1500){
			}
			else{
				this.eIsPressed = true;
			}
			break;
		case KeyEvent.VK_S:
			if(!status.isGameStarted() && !status.isGameOver() && !status.isGameStarting() && !status.isGameWon() && !status.isLevelWon()){
				JOptionPane.showMessageDialog( null, 
						"Item:                Price\r\n"+
								"\r\n"+
								"Extra Lives(10):      1500\r\n"+ 
								"Power Shot: 		   1000\r\n"+
								"Extra life:		   150\r\n"+
								"\r\n");

			}
			else{
			}
			break;
		case KeyEvent.VK_I:
			if(!status.isGameStarted() && !status.isGameOver() && !status.isGameStarting() && !status.isGameWon() && !status.isLevelWon()){
				JOptionPane.showMessageDialog( null, 
						"Power Up:     Explanation\r\n"+
								"\r\n"+
								"Extra Lives:      Gives 1 for 150 or 10 for 1500 extra livese (Extra Life per second)\r\n"+ 
								"                           (Press E to buy 10, or H to buy 1, limit of 10 lives per second.)\r\n" +
								"Power Shot:  Activates the Power Shot which kills the asteroid in one hit\r\n"+
						"                           (Press Q to buy for 50, afterwards press Q to fire.)\r\n"+
								"Press G for immortality. Press V + Space to change direction of bullets.\r\n"+
						"Press N to skip level. Press F for a higher fire rate.\r\n");

			}
			else{
			}
			break;

		case KeyEvent.VK_Q:
			if(!status.isGameStarted() && !status.isGameOver() && !status.isGameStarting() && !status.isGameWon() && !status.isLevelWon()){		
			}
			else{
				this.qIsPressed= true;
			}
			break;

		case KeyEvent.VK_H:
			this.hIsPressed= true;
			break;
		
		case KeyEvent.VK_N:
			this.nIsPressed = true;
			break;
		case KeyEvent.VK_G:
			this.gIsPressed = true;
			break;
		case KeyEvent.VK_V:
			this.aimUpIsPressed = true;
			break;
		case KeyEvent.VK_F:
			this.fIsPressed = true;
			break;
		}


		e.consume();
	}

	/**
	 * Handle a key release event.
	 */
	public void keyReleased(KeyEvent e) {
		switch(e.getKeyCode()){
		case KeyEvent.VK_UP:
			this.jumpIsPressed = false;
			break;
		case KeyEvent.VK_DOWN:
			this.downIsPressed = false;
			break;
		case KeyEvent.VK_LEFT:
			this.leftIsPressed = false;
			break;
		case KeyEvent.VK_RIGHT:
			this.rightIsPressed = false;
			break;
		case KeyEvent.VK_SPACE:
			this.spaceIsPressed = false;
			break;
		case KeyEvent.VK_SHIFT:
			this.shiftIsPressed = false;
			MegaMan megaMan = gameLogic.getMegaMan(); 
			megaMan.setSpeed(megaMan.getDefaultSpeed());
			break;
		case KeyEvent.VK_E:
			this.eIsPressed = false;
			break;
		case KeyEvent.VK_Q:
			this.qIsPressed = false;
			break;

		case KeyEvent.VK_H:
			this.hIsPressed = false;
			break;
			
		case KeyEvent.VK_N:
			this.nIsPressed = false;
			break;
		case KeyEvent.VK_G:
			this.gIsPressed = false;
			break;
		case KeyEvent.VK_V:
			this.aimUpIsPressed= false;
			break;
		case KeyEvent.VK_F:
			this.fIsPressed = false;
			break;
		}
		e.consume();
	}

	public void keyTyped(KeyEvent e) {
		// not used
	}

	public boolean getSpace(){
		return spaceIsPressed;
	}
	
	public boolean getUp(){
		return aimUpIsPressed;
	}

	public int getMute(){
		return mute;
	}
	public boolean getN(){
		return nIsPressed;
	}
}
