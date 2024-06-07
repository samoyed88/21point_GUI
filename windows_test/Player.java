package windows_test;

public class Player {
    private int num;
    private int score;
    private int a;
    public Player() {
        this.num = 0;
        this.score = 10;
        this.a=0;
    }

    public void numAdd(int num) {
        this.num += num;
    }
    
    public int getNum() {
    	if(this.num>21) {
    		while(a>=1) {
    			this.num-=10;
    			a--;
    		}
    	}
        return this.num;
    }

    public void numReturn() {
        this.num = 0;
        this.a=0;
    }
    
    public void A(int a) {
    	this.a+=a;
    }

    public void scoreCal(int dealerNum) {
        if (this.num > 21) {
            this.score-=2;
        }else if(this.num<dealerNum && dealerNum<=21){
        	this.score-=2;
        }else if (dealerNum > 21 || this.num > dealerNum) {
            this.score+=2;
        }else if(dealerNum==this.num) {
        	this.score++;
        }
    }

    public int getScore() {
        return this.score;
    }
}
