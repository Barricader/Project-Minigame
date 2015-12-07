package newserver;

import gameobjects.PongBall;
import util.Keys;
import util.MiniGames;
import util.NewJSONObject;

public class ServerPongBall extends Thread {
	private PongBall pongBall;
	private MiniGameManager miniMngr;
	
	public ServerPongBall(MiniGameManager miniMngr) {
		this.miniMngr = miniMngr;
		pongBall = new PongBall();
	}
	
	public void run() {
		while (MiniGames.names[miniMngr.getLastMini()].equals("pong")) {
			System.out.println("SERVER PONG BALL RUNNING!");
			pongBall.x += pongBall.getXVel();
			
			if (pongBall.x >= 600 || pongBall.x <= 0) {
				pongBall.setXVel(pongBall.getXVel() * -1);
			}
			
			NewJSONObject obj = new NewJSONObject(-1, Keys.Commands.MINI_UPDATE);
			obj.put(Keys.NAME, "pong");
			obj.put("objectOnlyUpdate", "ball");
			obj.put("ball", pongBall.toJSONObject());
			System.out.println("should be echoing ball!");
			miniMngr.getServerDir().getServer().echoAll(obj);
			
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
