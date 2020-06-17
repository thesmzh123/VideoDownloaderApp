package com.video.downloading.app.downloader.online.app.utils;

public class JavascriptNotation {
    public static String value = "javascript:" +
            "var e=0;\n" +
            "window.onscroll=function()\n" +
            "{\n" +
            "\tvar ij=document.querySelectorAll(\"video\");\n" +
            "\t\tfor(var f=0;f<ij.length;f++)\n" +
            "\t\t{\n" +
            "\t\t\tif((ij[f].parentNode.querySelectorAll(\"img\")).length==0)\n" +
            "\t\t\t{\n" +
            "\t\t\t\tvar nextimageWidth=ij[f].nextSibling.style.width;\n" +
            "\t\t\t\tvar nextImageHeight=ij[f].nextSibling.style.height;\n" +
            "\t\t\t\tvar Nxtimgwd=parseInt(nextimageWidth, 10);\n" +
            "\t\t\t\tvar Nxtimghght=parseInt(nextImageHeight, 10); \n" +
            "\t\t\t\tvar DOM_img = document.createElement(\"img\");\n" +
            "\t\t\t\t\tDOM_img.height=\"68\";\n" +
            "\t\t\t\t\tDOM_img.width=\"68\";\n" +
            "\t\t\t\t\tDOM_img.style.top=(Nxtimghght/2-20)+\"px\";\n" +
            "\t\t\t\t\tDOM_img.style.left=(Nxtimgwd/2-20)+\"px\";\n" +
            "\t\t\t\t\tDOM_img.style.position=\"absolute\";\n" +
            "\t\t\t\t\tDOM_img.src = \"https://image.ibb.co/kobwsk/one.png\"; \n" +
            "\t\t\t\t\tij[f].parentNode.appendChild(DOM_img);\n" +
            "\t\t\t}\t\t\n" +
            "\t\t\tij[f].remove();\n" +
            "\t\t} \n" +
            "\t\t\te++;\n" +
            "};" +
            "var a = document.querySelectorAll(\"a[href *= 'video_redirect']\");\n" +
            "for (var i = 0; i < a.length; i++) {\n" +
            "    var mainUrl = a[i].getAttribute(\"href\");\n" +
            "  a[i].removeAttribute(\"href\");\n" +
            "\tmainUrl=mainUrl.split(\"/video_redirect/?src=\")[1];\n" +
            "\tmainUrl=mainUrl.split(\"&source\")[0];\n" +
            "    var threeparent = a[i].parentNode.parentNode.parentNode;\n" +
            "    threeparent.setAttribute(\"src\", mainUrl);\n" +
            "    threeparent.onclick = function() {\n" +
            "        var mainUrl1 = this.getAttribute(\"src\");\n" +
            "         mJava.getData(mainUrl1);\n" +
            "    };\n" +
            "}" +
            "var k = document.querySelectorAll(\"div[data-store]\");\n" +
            "for (var j = 0; j < k.length; j++) {\n" +
            "    var h = k[j].getAttribute(\"data-store\");\n" +
            "    var g = JSON.parse(h);\nvar jp=k[j].getAttribute(\"data-sigil\");\n" +
            "    if (g.type === \"video\") {\n" +
            "if(jp==\"inlineVideo\")" +
            "{" +
            "   k[j].removeAttribute(\"data-sigil\");" +
            "}\n" +
            "        var url = g.src;\n" +
            "        k[j].setAttribute(\"src\", g.src);\n" +
            "        k[j].onclick = function() {\n" +
            "            var mainUrl = this.getAttribute(\"src\");\n" +
            "               mJava.getData(mainUrl);\n" +
            "        };\n" +
            "    }\n" +
            "\n" +
            "}";


    public static String valueResource="javascript:" +
            "var e=document.querySelectorAll(\"span\"); " +
            "if(e[0]!=undefined)" +
            "{" +
            "var fbforandroid=e[0].innerText;" +
            "if(fbforandroid.indexOf(\"Facebook\")!=-1)" +
            "{ " +
            "var h =e[0].parentNode.parentNode.parentNode.style.display=\"none\";" +
            "} " +
            "}" +
            "var installfb=document.querySelectorAll(\"a\");\n" +
            "for (var hardwares = 0; hardwares < installfb.length; hardwares++) \n" +
            "{\n" +
            "\tif(installfb[hardwares].text.indexOf(\"Install\")!=-1)\n" +
            "\t{\n" +
            "\t\tvar soft=installfb[hardwares].parentNode.style.display=\"none\";\n" +
            "\n" +
            "\t}\n" +
            "}\n";

    public static String getValue="javascript:" +
            "var e=0;\n" +
            "window.onscroll=function()\n" +
            "{\n" +
            "\tvar ij=document.querySelectorAll(\"video\");\n" +
            "\t\tfor(var f=0;f<ij.length;f++)\n" +
            "\t\t{\n" +
            "\t\t\tif((ij[f].parentNode.querySelectorAll(\"img\")).length==0)\n" +
            "\t\t\t{\n" +
            "\t\t\t\tvar nextimageWidth=ij[f].nextSibling.style.width;\n" +
            "\t\t\t\tvar nextImageHeight=ij[f].nextSibling.style.height;\n" +
            "\t\t\t\tvar Nxtimgwd=parseInt(nextimageWidth, 10);\n" +
            "\t\t\t\tvar Nxtimghght=parseInt(nextImageHeight, 10); \n" +
            "\t\t\t\tvar DOM_img = document.createElement(\"img\");\n" +
            "\t\t\t\t\tDOM_img.height=\"68\";\n" +
            "\t\t\t\t\tDOM_img.width=\"68\";\n" +
            "\t\t\t\t\tDOM_img.style.top=(Nxtimghght/2-20)+\"px\";\n" +
            "\t\t\t\t\tDOM_img.style.left=(Nxtimgwd/2-20)+\"px\";\n" +
            "\t\t\t\t\tDOM_img.style.position=\"absolute\";\n" +
            "\t\t\t\t\tDOM_img.src = \"https://image.ibb.co/kobwsk/one.png\"; \n" +
            "\t\t\t\t\tij[f].parentNode.appendChild(DOM_img);\n" +
            "\t\t\t}\t\t\n" +
            "\t\t\tij[f].remove();\n" +
            "\t\t} \n" +
            "\t\t\te++;\n" +
            "};" +
            "var a = document.querySelectorAll(\"a[href *= 'video_redirect']\");\n" +
            "for (var i = 0; i < a.length; i++) {\n" +
            "    var mainUrl = a[i].getAttribute(\"href\");\n" +
            "  a[i].removeAttribute(\"href\");\n" +
            "\tmainUrl=mainUrl.split(\"/video_redirect/?src=\")[1];\n" +
            "\tmainUrl=mainUrl.split(\"&source\")[0];\n" +
            "    var threeparent = a[i].parentNode.parentNode.parentNode;\n" +
            "    threeparent.setAttribute(\"src\", mainUrl);\n" +
            "    threeparent.onclick = function() {\n" +
            "        var mainUrl1 = this.getAttribute(\"src\");\n" +
            "         mJava.getData(mainUrl1);\n" +
            "    };\n" +
            "}" +
            "var k = document.querySelectorAll(\"div[data-store]\");\n" +
            "for (var j = 0; j < k.length; j++) {\n" +
            "    var h = k[j].getAttribute(\"data-store\");\n" +
            "    var g = JSON.parse(h);var jp=k[j].getAttribute(\"data-sigil\");\n" +
            "    if (g.type === \"video\") {\n" +
            "if(jp==\"inlineVideo\")" +
            "{" +
            "   k[j].removeAttribute(\"data-sigil\");" +
            "}\n" +
            "        var url = g.src;\n" +
            "        k[j].setAttribute(\"src\", g.src);\n" +
            "        k[j].onclick = function() {\n" +
            "            var mainUrl = this.getAttribute(\"src\");\n" +
            "               mJava.getData(mainUrl);\n" +
            "        };\n" +
            "    }\n" +
            "\n" +
            "}";
}
