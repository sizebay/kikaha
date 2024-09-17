package kikaha.cloud.aws.lambda;

import static org.junit.Assert.assertEquals;

import java.util.Collections;
import io.undertow.server.handlers.Cookie;
import org.junit.Test;

/**
 * Unit tests for AmazonLambdaRequest.
 */
public class AmazonLambdaRequestTest {

	final static String HUGE_COOKIE = "aws-target-static-id=1484592943055-723650; aws-business-metrics-last-visit=1484930233913; aws-session-id=146-2620512-4871931; aws-session-id-time=2122543762l; appstore-devportal-locale=en_US; session-id=136-1421104-9601940; session-id-time=1493750711l; ubid-main=133-0199856-8030424; aws_lang=en; s_sq=%5B%5BB%5D%5D; aws-ubid-main=163-5860719-2940727; aws-session-token=\"WVBjh5VCZ1mnVgIy7IUdOvKgt3liFCgwUWecxbuHFZUYee6qVrcjzhQ1zCM7pKBwMiR6WGdSZ0fn5X6LM7JUOGujQiw/N7XV3YxuUIA9oEvGslqLwmxtP8UEtgcwS8yZvHf+DJP+HW0KyuGB7S/bWANy+zdT5VBP/WcpLn3DRZTWkCc2jcEWY+Zl+BCv0TkBcLZDKX6s84mj6tgGYih2IzDhN+6KBwrEzfyUIN+L04o=\"; aws-x-main=\"HhXLhuq2kMyEDGhZ?haXFtf5z10RuOsXIuNnAiY9DYOZ1KLkDXrxb50Ap5Jgv7V?\"; __utmv=194891197.%22HhXLhuq2kMyEDGhZ%3FhaXFtf5z10RuOsXIuNnAiY9DYOZ1KLkDXrxb50Ap5Jgv7V%3F%22; __utmz=194891197.1493293886.12.5.utmccn=(referral)|utmcsr=amazon.com|utmcct=/ap/signin|utmcmd=referral; __utma=194891197.1837514991.1491338204.1493295934.1493301527.14; __utmc=194891197; pN=36; s_pers=%20s_vnum%3D1495831459135%2526vn%253D2%7C1495831459135%3B%20s_invisit%3Dtrue%7C1493303825268%3B%20s_nr%3D1493302025275-Repeat%7C1501078025275%3B; s_sess=%20s_cc%3Dtrue%3B%20s_sq%3D%3B; aws-mkto-trk=id%3A112-TZM-766%26token%3A_mch-aws.amazon.com-1484592945292-63231; _mkto_trk=id:112-TZM-766&token:_mch-aws.amazon.com-1484592945292-63231; aws-target-visitor-id=1484592943059-91745.20_72; aws-target-data=%7B%22support%22%3A%221%22%7D; aws-first-visit-2=0; s_vn=1516128943482%26vn%3D67; aws-doc-toc-pos=0; aws-doc-toc-url=http://docs.aws.amazon.com/lambda/latest/dg/; s_depth=1; s_cc=true; s_fid=3D5DEBA68B8F168E-33470FF6F2196361; s_dslv=1493317357644; s_dslv_s=Less%20than%201%20day; s_invisit=true; s_nr=1493317357649-Repeat; regStatus=registered; c_m=undefinedwww.google.com.brSearch%20Engine";

	@Test
	public void ensureCanGetCookies() throws Exception {
		final AmazonLambdaRequest request = new AmazonLambdaRequest();
		request.headers = Collections.singletonMap( "Cookie", HUGE_COOKIE );

		final Cookie cookie = request.getCookies().get( "aws-business-metrics-last-visit" );
		assertEquals( "aws-business-metrics-last-visit", cookie.getName() );
		assertEquals( "1484930233913", cookie.getValue() );
	}

}