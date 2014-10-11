package presenters;


public class DemoPresenter extends Presenter {
	
	public interface Demonstrator {
		
	}
	
	private Demonstrator demonstrator;
	
	public DemoPresenter(Demonstrator d) {
		demonstrator = d;
	}
	
	
	
}
