
<%@ page import="com.sridama.eztrack.tcp.*"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page import="java.util.*"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>

<html>
<!--  
                        Jsp init method to initialize threads which will start listening on provided tcp port .to read the tags.and check the status of the tags
                   -->

<%!
                     public HashMap < String , RFIDModel >  tMap = new HashMap<String , RFIDModel > () ;
                     HashMap < String , RFIDModel >  oldtMap = new HashMap<String , RFIDModel > () ;
                     HashMap < String , RFIDModel >  originaltMap = new HashMap<String , RFIDModel > () ;
                     
                     RFIDReader obj1 = new RFIDReader  () ;
                     
                     public void jspInit() 
                     {
                    	try
                    	{
	                        obj1.startRead( obj1 );
	                    	
                    	}
                   	     catch ( Exception e ) {
                         	e.printStackTrace() ;
                    	} 
                     }

                     %>

<head>
<meta charset="utf-8" />
<meta http-equiv="refresh" content="5">
<!-- Always force latest IE rendering engine (even in intranet) & Chrome Frame
                Remove this if you use the .htaccess -->
<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">

<title>index</title>
<meta name="description" content="">
<meta name="author" content="ravindranath.m">

<meta name="viewport" content="width=device-width; initial-scale=1.0">

<!-- Replace favicon.ico & apple-touch-icon.png in the root of your domain and delete these references -->
<link rel="shortcut icon" href="/favicon.ico">
<link rel="apple-touch-icon" href="/apple-touch-icon.png">
<link rel="stylesheet" href="bootstrap-3.0.3/dist/css/bootstrap.min.css" />
<link rel="stylesheet"
	href="font-awesome-4.0.3/css/font-awesome.min.css" />
<link rel="stylesheet" href="jquery-ui-1.10.3/themes/base/jquery-ui.css" />
<link rel="stylesheet" href="typeahead/css/typeahead-bootstrap.css" />
<link rel="stylesheet" href="select2-3.4.4/select2.css" />
<link rel="stylesheet"
	href="DataTables-1.9.4/media/css/jquery.dataTables.css" />
<!-- <link rel="stylesheet" href="DT_Bootstrap/css/DT_bootstrap.css" /> -->
<link rel="stylesheet"
	href="DataTables-1.9.4/media/css/dataTables_bootstrap.css" />
<link rel="stylesheet" href="jquery.qtip.custom/jquery.qtip.min.css" />
<link rel="stylesheet" href="css3-treevew-no-javascript/tree-view.css" />
<link rel="stylesheet" href="css/scrollable-tbl.css" />
<!-- <link rel="stylesheet" href="typeahead/css/tt-fix.css" /> -->
<link rel="stylesheet" href="css/explore.css" />
<title>RFID Based Inventory Tracking</title>
<link rel="shortcut icon" href="img/icon.jpg" type="image/x-icon">
<!--       <link rel="stylesheet" href="http://code.jquery.com/ui/1.10.3/themes/smoothness/jquery-ui.css" />
                    <script src="http://code.jquery.com/jquery-1.9.1.js"></script>
                    <script src="http://code.jquery.com/ui/1.10.3/jquery-ui.js"></script> -->
<link rel="stylesheet" href="/resources/demos/style.css" />
<style type="text/css" title="currentStyle">
@import "/media/css/demo_page.css";

@import "/media/css/demo_table_jui.css";

@import
	"media/examples_support/themes/smoothness/jquery-ui-1.8.4.custom.css";
</style>



</head>


<body>
	<%
                        // Set refresh, autoload time as 3 seconds
                       int count;  
                   		 obj1.startRead(obj1);
                        System.out.println( "Evaluating date now" );
                        tMap = (HashMap)obj1.getTagList();
                       System.out.println("old tmap is"+oldtMap.size()+"and new tmap is"+tMap.size());
                     if(oldtMap.size() == 0 || (oldtMap.size() < tMap.size())){
                    	 // System.out.println("--------------------------------------in zero");
                    	 	originaltMap = (HashMap) tMap.clone();
                    		Set<String> addedKeys = new HashSet<String>(tMap.keySet());
                          	addedKeys.removeAll(oldtMap.keySet());
    						
    						System.out.println("added " + addedKeys);
    						request.setAttribute( "addedkeys",addedKeys );
    						oldtMap = (HashMap) originaltMap.clone();
    						
                    	  
                     	}
                     if(oldtMap.size() > tMap.size()){
                    	 originaltMap = (HashMap) tMap.clone();
                      	Set<String> removedKeys = new HashSet<String>(oldtMap.keySet());
                      	removedKeys.removeAll(tMap.keySet());

                      	/* Set<String> addedKeys = new HashSet<String>(tMap.keySet());
                      	addedKeys.removeAll(oldtMap.keySet()); */
						
						System.out.println("removed " + removedKeys);
                      	request.setAttribute( "removedkeys",removedKeys );
						oldtMap = (HashMap) originaltMap.clone();
						
                     }
      						Date date = new Date();
                        System.out.println("laglist"+tMap);
                        request.setAttribute( "taglist", tMap);
                      
					%>


<div class="product-hdr">
	<div class="row">
		<div id="hdr-row" class="col-lg-12">
			
				<img class="img-logo" src="resources/sridama_new_logo.png"
					height="60" />
				<div class="product-title">
					ezTRACK - RFID Inventory Management System <span class="version-info">version
						1.0.0</span>
				</div>
			
		</div>
	</div>
</div>
	<div class="btn-group pull-right product-hdr" id="login-div">
		<!-- <div class="btn-group"> -->
		<a class="btn dropdown-toggle btn-primary" data-toggle="dropdown"
			id="login-name" href="#"> <i class="fa fa-user fa-fw"></i>
			<p class="inline">Ravindranath M</p> <span class="fa fa-caret-down"></span>
		</a>
		<!-- <a class="btn dropdown-toggle" data-toggle="dropdown" href="#"> <span class="fa fa-caret-down"></span></a> -->
		<ul class="dropdown-menu">
			<li><a href="#"><i class="fa fa-pencil fa-fw"></i> Change
					Password</a></li>
			<li class="divider"></li>
			<li><a href="#" id="signout"><i class="fa fa-sign-out"></i>
					Signout</a></li>
		</ul>
		<!-- </div> -->
	</div>

	<div id="centre" style="width: 100%; height: 71%; margin-top: 20px;">



		<div class="container">
			<div id="content-row" class="row">
				
				<div class="col-sm-12 content-data">
					<div id="canvas">
						<div id="inventory-tracking" class="cpanel cpanel-info shadow">
							<div class="cpanel-heading">
								Inventory Tracking <a href="eztrack.html" class="pull-right panel-toggler"><span
									class="glyphicon glyphicon-remove pull-right"></span></a>
									
							</div>
							<!--  <marquee style="text-align: left; margin-left: 20px; color: blue;"><i>RFID INVERTORY TRACKING<i></marquee>  -->


							<table cellpadding="0" cellspacing="0" border="0" class="display"
								id="example" style="font-size: 16; height: 10%;">
								<thead>
									<tr>
										<th><b> Jewels Scanned </b></th>
										<th><b> Direction </b></th>
										<th><b> Weight in gms </b></th>
										<th><b> Scan Count </b></th>
										<th><b> Add Details </b></th>
										<th><b> Status </b></th>
										<th><b> location </b></th>
									</tr>
								</thead>
								<tbody>
									<c:forEach var="tag" items="${taglist}">
										<tr class="gradeX">
											<td class="center">${tag.key.substring(0, 24)}</td>
											<td class="center">${tag.key.substring(25, tag.key.length())}</td>
											<td class="center">1.2</td>
											<td class="center">${tag.value.count}</td>
											<td class="center"><a href="AddDetails.jsp?id=${tag.key}"> add </a></td>
											<td class="center">${tag.value.status}</td>
											<td class="center">${tag.value.location}</td>
										</tr>
									</c:forEach>
								</tbody>
							</table>
							<div>
								<p>
									<u><b>Inventory Report</b></u>
								</p>
								<p>addedkeys are: ${addedkeys}</p>
								<p>removedkeys are: ${removedkeys}</p>

							</div>

						</div>

					</div>

				</div>

				<div id="footer"
					style="background-color: #4d4dae; clear: both; text-align: center; height: 5%;">
					<p style="color: white">@copyright 2013 reserved to sridama
						group</p>
				</div>
				<div class="clearfix visible-xs"></div>
				<div class="clearfix visible-sm"></div>
				<div class="clearfix visible-lg"></div>
				<div class="clearfix visible-md"></div>
			</div>
		</div>
</div>


<script type="text/javascript" charset="utf-8"></script>
<script src="js/jquery-2.0.3.min.js"></script>
<script src="bootstrap-3.0.3/dist/js/bootstrap.min.js"></script>
<script src="jquery-ui-1.10.3/ui/jquery-ui.js"></script>
<script src="typeahead/js/typeahead.min.js"></script>
<script src="hogan.js-master/web/builds/2.0.0/hogan-2.0.0.js"></script>
<script src="js/jquery.numeric.js"></script>
<script src="select2-3.4.4/select2.js"></script>
<script src="js/recursiveLoader.js"></script>
<script src="DataTables-1.9.4/media/js/jquery.dataTables.min.js"></script>
<!-- <script src="DT_Bootstrap/js/DT_bootstrap.js"></script> -->
<script src="DataTables-1.9.4/media/js/datatables_bootstrap.js"></script>
<script src="bpopup/js/jquery.bpopup.min.js"></script>
<script src="jquery.qtip.custom/jquery.qtip.min.js"></script>
<script src="js/jquery.cookie.js"></script>
<script src="js/jquery.maskedinput.min.js.txt"></script>
<script src="js/jquery.validate.min.js"></script>
<script src="js/jquery.filter_input.js"></script>
<script src="amcharts_3.4.3.free/amcharts/amcharts.js"></script>
<script>
                            $(document).ready(function() {
                                debugger;
                                    $('#example').dataTable({
                                    	"bFilter": true ,
                            			"sSearch": true   
                                    } );
                            } );
                    </script>


</body>
</html>
</body>
</html>