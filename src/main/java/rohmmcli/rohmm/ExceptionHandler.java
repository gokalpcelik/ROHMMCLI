package rohmmcli.rohmm;

public class ExceptionHandler {

	protected static void IncorrectModelFormat() throws Exception {
		throw new Exception("Model file format is not correct...");
	}
}
