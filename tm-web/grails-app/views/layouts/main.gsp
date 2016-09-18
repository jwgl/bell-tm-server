<!doctype html>
<html lang="en" class="no-js">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <title><g:layoutTitle default="BELL"/></title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link rel="shortcut icon" href="${resource(dir: 'images', file: 'favicon.ico')}" type="image/x-icon">
    <link rel="stylesheet" href="/static/css/lib/bootstrap.min.css">
    <link rel="stylesheet" href="/static/css/lib/bootstrap-submenu.min.css">
    <link rel="stylesheet" href="/static/css/lib/font-awesome.min.css">
    <link rel="stylesheet" href="/static/css/app/main.css">
    <g:layoutHead/>
</head>
<body>
    <header class="navbar navbar-static-top navbar-light bg-faded">
        <div class="container">
            <nav>
                <div class="clearfix">
                    <button class="navbar-toggler pull-xs-right hidden-sm-up" type="button" data-toggle="collapse" data-target="#bd-main-nav">
                        &#9776;
                    </button>
                </div>
                <div class="collapse navbar-toggleable-xs" id="bd-main-nav">
                    <div class="navbar-brand logo"></div>
                    <ul class="nav navbar-nav">
                        <b:menu root="main"></b:menu>
                    </ul>
                    <ul class="nav navbar-nav pull-xs-right">
                        <b:menu root="user"></b:menu>
                        <li class="nav-item">
                            <form id="logoutForm" action="/logout" method="post"></form>
                            <a id="logout" class="nav-link" href="#">${message(code:'menu.user.logout')}</a>
                        </li>
                    </ul>
                </div>
            </nav>
        </div>
    </header>
    <div class="container">
        <g:layoutBody />
    </div>
    <footer></footer>
    <script src="/static/js/lib/moment-with-locales.min.js"></script>
    <script src="/static/js/lib/jquery.min.js"></script>
    <script src="/static/js/lib/tether.min.js"></script>
    <script src="/static/js/lib/bootstrap.min.js"></script>
    <script src="/static/js/lib/bootstrap-submenu.min.js"></script>
    <script src="/static/js/app/common.js"></script>
    <script src="/static/js/app/angular.js"></script>
    <asset:deferredScripts/>
    <script src="/static/js/app/logo.js"></script>
    <script src="/static/js/app/menu.js"></script>
</body>
</html>
