package kikaha.core.cdi;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CountDownApplication implements Application {

	volatile int number;

	@Override
	public void run() throws Exception {
		number--;
	}
}
