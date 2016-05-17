<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <title>教学管理系统</title>
    <meta name="keywords" content="北京师范大学珠海分校 教学管理系统">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="shortcut icon" href="${resource(dir: 'images', file: 'favicon.ico')}" type="image/x-icon">
    <link rel="stylesheet" type="text/css" href="/static/css/lib/bootstrap.min.css"/>
    <link rel="stylesheet" type="text/css" href="/static/css/lib/font-awesome.min.css"/>
    <style>
        body {
            padding-top: 40px;
            padding-bottom: 40px;
            background-color: #eee;
        }

        .form-signin {
            max-width: 330px;
            padding: 15px;
            margin: 0 auto;
        }

        .form-signin >.form-signin-heading,
        .form-signin > .btn-group,
        .form-signin > button {
            margin-bottom: 10px;
        }

        .form-signin .checkbox {
            font-weight: normal;
        }

        .form-signin .form-control:focus {
            z-index: 2;
        }

        .form-signin .input-group {
            margin-bottom: 10px;
        }

        .form-signin input.form-control,
        .form-signin glyphicon {
            font-size: 16px;
            padding: 5px 12px;
            height: 38px;
        }

        .not-support {
            max-width: 330px;
            padding: 15px;
            margin: 0 auto;
        }
    </style>
</head>
<body>
<div class="container">
    <form class="form-signin" action='/uaa/login' method='POST' id='loginForm' autocomplete='off'>
        <div class="input-group">
            <span class="input-group-addon"><span class="fa fa-user fa-fw"></span></span>
            <input type='text' class='form-control' name='username' id='username' placeholder="用户名" />
        </div>
        <div class="input-group">
            <span class="input-group-addon"><span class="fa fa-lock fa-fw"></span></span>
            <input type='password' class='form-control' name='password' id='password' placeholder="密码" />
        </div>
        <button class="btn btn-lg btn-primary btn-block" type="submit">登录</button>
        <g:if test='${flash.message}'>
            <div class='alert alert-danger'>
                ${flash.message}
            </div>
        </g:if>
    </form>
    <!--[if lt IE 11 ]>
        <div class="alter alert-warning not-support">
        <p>不支持此版本的浏览器，请升级至<a href="http://www.microsoft.com/zh-cn/download/internet-explorer-8-details.aspx">IE8</a>或者使用<a
        href="http://firefox.com.cn/">Firefox</a>/<a href="http://www.google.cn/intl/zh-CN/chrome/">Chrome</a>。</p>
        <p>或者下载<a href="/download/FirefoxPortable.exe">Firefox绿色版</a>，可放在U盘中使用。</p>
        <style>
            form { display: none;}
        </style>
        </div>
    <![endif]-->
</div>
<asset:javascript src="auth.js"/>
</body>
</html>
