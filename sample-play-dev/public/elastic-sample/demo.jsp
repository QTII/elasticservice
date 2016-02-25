<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<title>ServerElastic v1.4 Demo</title>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<meta name="viewport" content="width=device-width, initial-scale=1"/>
		
		<!--  jQuery Mobile 1.4.2 CDN -->
<!-- 		<link rel="stylesheet" href="../html5/jqm/1.4.2/css/themes/default/jquery.mobile-1.4.2.min.css" /> -->
<!-- 		<script src="../html5/jq/jquery-1.9.1.min.js"></script> -->
<!-- 		<script src="../html5/jqm/1.4.2/js/jquery.mobile-1.4.2.min.js"></script> -->
		
		<!--  jQuery Mobile 1.3.2 CDN -->
		<link rel="stylesheet" href="../html5/jqm/1.3.2/css/themes/default/jquery.mobile-1.3.2.min.css" />
		<script src="../html5/jq/jquery-1.9.1.min.js"></script>
		<script src="../html5/jqm/1.3.2/js/jquery.mobile-1.3.2.min.js"></script>
</head>
<BODY>
<H1>ServerElastic v1.5 Demo</H1>
<P>
	<div data-role="page">
		<div data-role="header">
			<h1>ServerElastic v1.5 Demo</h1>
		</div>
		<div data-role="content">
			<div id="built-in" data-inline="true">
				<h3>Preparing Data</h3>
				<OL>
					<LI><a charset="UTF-8" href="Builtin_DefaultService_1CreateTestTable.html" data-ajax="false">Create test table 'T_TEST'</a></LI>
					<LI><a charset="UTF-8" href="Sample031_MultiInsertService.html" data-ajax="false">Insert data into 'T_TEST'</a></LI>
				</OL>
				<h3>SQL Query</h3>
				<OL>					
					<LI><a charset="UTF-8" href="Builtin_DefaultService_SingleQuery.html" data-ajax="false">Request a SQL query</a></LI>
					<LI><a charset="UTF-8" href="Builtin_DefaultService_TransactionQuery.html" data-ajax="false">Request two SQLs in a transaction</a></LI>
				</OL>
				<h3>Binary Response (Curl)</h3>
				<OL>
					<LI>
						Local : <b>file:///<%= getServletContext().getRealPath("/") %>elastic-sample\curl\start.curl</b><br>
						<font size=2>
						Step 1: Copy the URL above and paste into browser's address input box and hit enter.<br> 
						Step 2: Go and submit 'Log in'<br>
						Step 3: Go and submit 'Binary response'<br>
						</font>
						</LI>
						
					<LI>Remote : <a charset="UTF-8" href="curl/start.curl" data-ajax="false">curl/start.curl (Curl License is required)</a></LI>
				</OL>
				<h3>WebSocket Service (Server Push)</h3>
				<OL>
					<LI><a charset="UTF-8" href="WebSocket.html" data-ajax="false">WebSocket</a></LI>
				</OL>
				<h3>Background Scheduling Job Service</h3>
				<OL>
					<LI><a charset="UTF-8" href="Builtin_SqlRunCronjob.html" data-ajax="false">Run a SQL query in background</a></LI>
					<LI><a charset="UTF-8" href="Builtin_SystemCallCronjob.html" data-ajax="false">Run a system command in background</a></LI>
				</OL>
				<h3>Login Service</h3>
				<OL>
					<LI><a charset="UTF-8" href="Builtin_LoginService.html" data-ajax="false">Log in</a></LI>
					<LI><a charset="UTF-8" href="Builtin_SessionService.html" data-ajax="false">Query session informations</a></LI>
					<LI><a charset="UTF-8" href="Builtin_SessionTimeoutService.html" data-ajax="false">Set session's expiration time (seconds)</a></LI>
					<LI><a charset="UTF-8" href="Builtin_LogoutService.html" data-ajax="false">Log out</a></LI>
				</OL>
				<h3>Mobile</h3>
				<OL>
					<LI><a charset="UTF-8" href="Sample13_jsonp.html" data-ajax="false">JSONP</a></LI>
				</OL>
			</div>
		</div>
	</div>
</BODY>
</html>
