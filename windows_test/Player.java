package windows_test;

public class Player {
    private int num;
    private int score;

    public Player() {
        this.num = 0;
        this.score = 10;
    }

    public void numAdd(int num) {
        this.num += num;
    }
    
    public int getNum() {
        return this.num;
    }

    public void numReturn() {
        this.num = 0;
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
