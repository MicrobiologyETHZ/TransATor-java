<!DOCTYPE html>
<!--[if lt IE 7]> <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]> <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]> <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!-->
<html class="no-js"> <!--<![endif]-->
<%@ page import="java.net.URLEncoder" %>
<%@ page import="java.util.List" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <title>transAT-PKS Prediction Tool</title>
    <meta name="description" content="">
    <meta name="viewport" content="width=device-width">

    <!-- Place favicon.ico and apple-touch-icon.png in the root directory -->

    <link rel="stylesheet" href="css/normalize.css">
    <script src="js/vendor/modernizr-2.6.2.min.js"></script>

    <!-- BioJS -->
    <script language="JavaScript" type="text/javascript" src="js/Biojs.js"></script>
    <script language="JavaScript" type="text/javascript" src="js/Biojs.FeatureViewer.js"></script>

    <!-- Local -->
    <script src="js/dependencies/jquery/jquery-1.7.2.min.js"></script>
    <script src="js/dependencies/jquery/jquery-ui-1.8.2.custom.min.js"></script>
    <script src="js/dependencies/jquery/jquery.tooltip.js"></script>
    <script src="js/dependencies/graphics/raphael.js"></script>
    <script src="js/dependencies/graphics/canvg.js"></script>
    <script src="js/dependencies/graphics/rgbcolor.js"></script>


    <!-- CSS -->
    <link rel="stylesheet" href="js/dependencies/jquery/jquery-ui-1.8.2.css"/>
    <link rel="stylesheet" href="js/dependencies/jquery/jquery.tooltip.css">
    <link rel="stylesheet" href="js/dependencies/jquery/images/ui-bg_flat_0_aaaaaa_40x100.png">
    <link rel="stylesheet" href="js/dependencies/jquery/images/ui-bg_flat_75_ffffff_40x100.png">
    <link rel="stylesheet" href="js/dependencies/jquery/images/ui-bg_glass_65_ffffff_1x400.png">
    <link rel="stylesheet" href="js/dependencies/jquery/images/ui-bg_glass_75_dadada_1x400.png">
    <link rel="stylesheet" href="js/dependencies/jquery/images/ui-bg_glass_75_e6e6e6_1x400.png">
    <link rel="stylesheet" href="js/dependencies/jquery/images/ui-bg_highlight-soft_75_cccccc_1x100.png">
    <link rel="stylesheet" href="js/dependencies/jquery/images/ui-icons_222222_256x240.png">
    <link rel="stylesheet" href="js/dependencies/jquery/images/ui-icons_454545_256x240.png">

    <link rel="stylesheet" href="css/main.css">

    <script>
        $(function () {
            $("#accordionSequences").accordion();
        });
    </script>
</head>
<body>

<h1 class="textCentering"><i>Trans</i>-AT PKS derived polyketide prediction results</h1>

<p class="textCentering">The annotation of the different <i>trans</i>-AT PKS KS clades on the submitted sequences
    produces
    the following structure:</p>
<img class="resultingMol" id="pkMol" path="<%= request.getSession().getAttribute("tmp") %>">

<p class="textCentering" id="pkSmiles"></p>

<p class="textCentering">
    The annotation for each sequence submitted can be seen in the sections below.
</p>
<div id="tabs">

    <ul>
        <li><a href="#accordionSequences">Viewer</a></li>
        <li><a href="#rawResult">KS Annotations</a></li>
    </ul>

    <div id="accordionSequences">
        <%
            int viewerNumber = 0;
            for (String identifier : (List<String>) request.getSession().getAttribute("identifers")) {
        %>
        <h3 id="headerView<%= viewerNumber%>" class="newHeaderForAccordion"><%= URLEncoder.encode(identifier, "UTF-8")%>
            -
            processing...</h3>
        <div class="seqResult" id="featureView<%= viewerNumber%>" viewerNumber="<%= viewerNumber%>"
             path="<%= request.getSession().getAttribute("tmp") %>" seqId="<%= URLEncoder.encode(identifier,"UTF-8")%>">
            <img src="img/ajax-loader.gif" id="waitingImg" class="waitingImage">
        </div>
        <% viewerNumber++;
        } %>
    </div>

    <div id="rawResult" class="textCentering">
        <p>In this section you find for every KS domain a list of predicted clades ordered by their e-value</p>
    </div>
</div>


<script>

    function groupBy(xs, key) {
        return xs.reduce(function (rv, x) {
            let v = key instanceof Function ? key(x) : x[key];
            let el = rv.find((r) => r && r.key === v);
            if (el) {
                el.values.push(x);
            } else {
                rv.push({key: v, values: [x]});
            }
            return rv;
        }, []);
    }

    window.onload = function () {

        var $j = jQuery.noConflict();

        $j("#tabs").tabs();

        $j(".seqResult").each(function () {
                var divObj = $j(this);
                $j.getJSON("rest/pkspredictor/query?path=" + $j(this).attr("path") + "&seqId=" + $j(this).attr("seqId"),
                    function (data) {
                        var json = data;
                        divObj.find("#waitingImg").hide();
                        var myPainter = new Biojs.FeatureViewer({
                            target: divObj.attr("id"),
                            json: json,
                            showPrintButton: false,
                            selectFeatureOnMouseClick: false,
                            dragSites: false,
                        });

                        $j("#dialog").dialog({
                            autoOpen: false,
                            height: 400,
                            width: 600,
                            modal: true,
                            buttons: {
                                "close": function(event) {
                                    $j("#dialog").dialog("close");
                                },
                                "copy": function(event) {
                                    // copy to clipboard. in-place, because creating a function anywhere silently breaks everything...
                                    var aux = document.createElement("input");
                                    aux.setAttribute("value", document.getElementById("dialog-content").innerHTML);
                                    document.body.appendChild(aux);
                                    aux.select();
                                    document.execCommand("copy");
                                    document.body.removeChild(aux);
                                    $j(event.target).find(".ui-button-text").text("done!");
                                    setTimeout(function() {
                                        $j(event.target).find(".ui-button-text").text("copy");
                                    }  , 1000 );
                                }
                            },
                            my: "center",
                            at: "center",
                            of: window,
                            close : function(){
                                $j("#dialog-content").text();
                            }
                        });

                        myPainter.onFeatureClick(
                            function (obj) {
                                $j("#dialog-content").text(data.querySequence.slice(obj.featureStart-1,obj.featureEnd-1));
                                $j("#dialog-content").css("word-wrap", "break-word");
                                $j("#dialog").dialog('open');
                            }
                        );

                        var viewNum = divObj.attr("viewerNumber");
                        $j("#headerView" + viewNum).html(divObj.attr("seqid"));

                        var features = json['featuresArray'];

                        // TODO: use the presence of the clusterId to filter out clades
                        var groupedFeatures = groupBy(features.filter(f => f.evidenceCode.startsWith("Clade")), "clusterId");

                        $j("#rawResult").append("<h3>" + divObj.attr("seqid") + "</h3>");

                        if (groupedFeatures.length === 0) {
                            $j("#rawResult").append("No Annotation");
                            return;
                        }

                        groupedFeatures
                            .map((group, index) =>
                                "<p>"
                                + "<h4>KS" + (index + 1) + "</h4>" +
                                group.values
                                    .sort((a, b) => a.y > b.y)
                                    .map((v, index) => "<span class=ks" + index + ">" + v.evidenceCode + " " + v.typeLabel + " (" + v.featureLabel + ")" + "</span>")
                                    .join('</br>') +
                                "</p>"
                            )
                            .forEach(code => $j('#rawResult').append(code));
                    }
                );
            }
        );

        $j('#pkMol').attr('src', 'rest/pkspredictor/structure?path=' + $j("#pkMol").attr("path"));

        $j.get('rest/pkspredictor/smiles?path=' + $j("#pkMol").attr("path"),
            function (data, status, response) {
                $j('#pkSmiles').text("SMILES: " + response.responseText);
            });
    };


</script>

<script src="js/plugins.js"></script>
<script src="js/main.js"></script>

<div id="dialog" title="Amino Acid Sequence of Selected Domain"><p id="dialog-content"></p></div>

</body>
</html>