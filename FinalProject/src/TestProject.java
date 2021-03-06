import java.io.IOException;
import java.net.URL;
import java.security.cert.*;
import java.util.HashMap;
import java.util.Map.Entry;
import javax.net.ssl.*;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet implementation class TestProject
 */
@WebServlet("/TestProject")
public class TestProject extends HttpServlet {
	private static final long serialVersionUID = 1L;

//	public KeywordList lst = new KeywordList();
//	public ArrayList<WebNode> nodeList = new ArrayList<WebNode>();
//	public Ranking rankResult;
	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public TestProject() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		/* 繞開SSL驗證 */
		try {
			trustAllHttpsCertificates();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		HostnameVerifier hv = new HostnameVerifier() {
			public boolean verify(String urlHostName, SSLSession session) {
				return true;
			}
		};
		HttpsURLConnection.setDefaultHostnameVerifier(hv);
		/* 繞開SSL驗證 */

		request.getSession().setMaxInactiveInterval(-1);

		HttpSession session = request.getSession();
		session.setMaxInactiveInterval(-1);

		response.setCharacterEncoding("UTF-8");
		request.setCharacterEncoding("UTF-8");
		response.setContentType("text/html");
		if (request.getParameter("keyword") == null) {
			String requestUri = request.getRequestURI();
			request.setAttribute("requestUri", requestUri);
//			request.getSession().setMaxInactiveInterval(-1);
//			System.out.println(request.getAttribute(requestUri)));
			request.getRequestDispatcher("Search.jsp").forward(request, response);
			return;
		}

		GoogleQuery google = new GoogleQuery(request.getParameter("keyword"));
		HashMap<String, String> query = google.query();
		DomainList domainList = new DomainList();
		KeywordList keywordList = new KeywordList();

		String[][] s = new String[query.size()][3];
		request.setAttribute("query", s);
		int num = 0;

		
		
		
		
		
		
		
		
		
		for (Entry<String, String> entry : query.entrySet()) {
			// Mutli Thread
			String title = entry.getKey();
			String url = entry.getValue();

			s[num][0] = title;
			s[num][1] = url;

			if (!domainList.getList().contains(new URL(url).getHost())) {
				s[num][2] = String.valueOf(-10.0);
				num++;
				continue;
			}

			WebTree tree = new WebTree(new WebPage(url, title));

			try {
//				tree.root.webPage.toFetch();
				tree.root.toSubPage();
			} catch (Exception e) {
				System.err.println(e.getMessage());
			}

			tree.setPostOrderScore(keywordList.getList());
			s[num][2] = String.valueOf(tree.root.nodeScore);
//			s[num][2] = String.valueOf(Math.random() * 100);
			// Mutli Thread
			num++;
		}

		// ***join***

		
		
		
		
		
		
		
		
		
		
		// Rank
		Ranking.rank(s);
		for (int i = 0; i < s.length; i++) {
			System.out.println(s[i][0] + ", " + s[i][1] + ", " + s[i][2]);
		}
//		test
		System.out.println("will show the result...");
		request.getRequestDispatcher("googleitem.jsp").forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

	/* 繞開SSL驗證 */
	private static void trustAllHttpsCertificates() throws Exception {
		TrustManager[] trustAllCerts = new TrustManager[1];
		TrustManager tm = new miTM();
		trustAllCerts[0] = tm;
		SSLContext sc = SSLContext.getInstance("SSL");
		sc.init(null, trustAllCerts, null);
		HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
	}

	static class miTM implements TrustManager, X509TrustManager {
		public X509Certificate[] getAcceptedIssuers() {
			return null;
		}

		public boolean isServerTrusted(X509Certificate[] certs) {
			return true;
		}

		public boolean isClientTrusted(X509Certificate[] certs) {
			return true;
		}

		public void checkServerTrusted(X509Certificate[] certs, String authType) throws CertificateException {
			return;
		}

		public void checkClientTrusted(X509Certificate[] certs, String authType) throws CertificateException {
			return;
		}
	}
	/* 繞開SSL驗證 */

}
