<%--starting scriplet 
if no response from servlet don't display anything
--%>
<%
    String textBox = (String) session.getAttribute("textBox");
    String result = (String) session.getAttribute("result");
    if(result == null){
        result = " ";
   }
   if(textBox == null){
       textBox = " ";
   }
%>

<!doctype html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">

    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.1.1/css/bootstrap.min.css" integrity="sha384-WskhaSGFgHYWDcbwN70/dfYBj47jz9qbsMId/iRN3ewGhXQFZCSftd1LZCfmhktB" crossorigin="anonymous">
    <title>Project 4</title>
    <script src="reset.js"></script>  
    <style>
        body{
            margin: 0;
            font-family: Arial, Helvetica, sans-serif;
            background-color: darkslategray;
        }
    </style>
</head>

<body>
    <div class="container-fluid ">
        <row class="row justify-content-center">
            <div class="text-center text-light col-sm-12 col-md-12 col-lg-12">
                <h1>Welcome to the Fall 2021 Project 4 Enterprise Database System</h1>
                <h2>A Servlet/JSP-based Multi-tiered Enterprise Application Using A Tomcat Container</h2>
                    <p>You are connected to the Project 4 Enterprise System database as a root user.</p>
                    <p>Please enter any valid SQL query or update command in the box below.</p>
            </div>
            
            <form action = "/Project4/SQLQueryServlet" method = "post" style="margin-top: 15px;" class="text-center">
                <div class="form-group row">
                    <div class=" col-sm-12 col-md-12 col-lg-12">
                        <textarea name="textBox" class="form-control" id="textBox" rows="8" cols="50"><%= textBox %></textarea>
                    </div>
                </div>

                <button style="margin-bottom: 17px;" type="submit" class="btn btn-light">Execute Command</button>
                <button style="margin-bottom: 17px;" type="reset"  class="btn btn-light" onClick="reset();">Reset Form</button>
                <button style="margin-bottom: 17px;" class="btn btn-light">Clear Results</button>
            </form>

            <div class="text-center text-light col-sm-12 col-md-12 col-lg-12">All of your execution results will appear below:</div>
        </row>
    </div>

    <div class="text-center text-light">
        <%-- jsp statement with out sql response--%>
        <%= result %>
    </div>
    
</body>
</html>