package helper;

//Point
	public class Point {
		private int x = 0;
		private int y = 0;
		//constructor
		public Point(int a, int b) {
			x = a;
			y = b;
		}
		public int getx() {
			return x;
		}
		public int gety() {
			return y;
		}
		public void setx(int x) {
			this.x=x;
		}
		public void sety(int y) {
			this.y=y;
		}
		public boolean isempty() {
			return (x == 0 && y==0)?true:false;
		}
		public Point(){
			x=0;y=0;
		}
		public void empty(){
			x=0;y=0;
		}
		public void setP(int x,int y){
			this.x = x;
			this.y = y;
		}
		public String getp(){
			return "(x , y) = ("+x+","+y+")";
		}
	}