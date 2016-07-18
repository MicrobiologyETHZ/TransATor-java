<!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!--> <html class="no-js"> <!--<![endif]-->
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <title>trans-AT PKS-derived Polyketide Prediction Tool</title>
    <meta name="description" content="">
    <meta name="viewport" content="width=device-width">

    <!-- Place favicon.ico and apple-touch-icon.png in the root directory -->

    <link rel="stylesheet" href="css/normalize.css">
    <link rel="stylesheet" href="css/main.css">
    <script src="js/vendor/modernizr-2.6.2.min.js"></script>
</head>
<body>
<!--[if lt IE 7]>
<p class="chromeframe">You are using an <strong>outdated</strong> browser. Please <a href="http://browsehappy.com/">upgrade your browser</a> or <a href="http://www.google.com/chromeframe/?redirect=true">activate Google Chrome Frame</a> to improve your experience.</p>
<![endif]-->

<!-- Add your site or application content here -->
<h1 class="textCentering"><i>trans</i>ATor web application</h1>

<p class="textCentering">
    The trans-AT polyketide synthase-derived structure predictor (transATor) is a genome mining tool that predicts the core structure of a trans-AT PKS derived polyketide polyketide based on the protein sequence of the corresponding biosynthetic gene cluster.
    For more information on transATor see below.
    Currently, only protein sequences in proper fasta format can be used as input.
</p>
<form action="ValidateInput" method="post" enctype="multipart/form-data">
<p class="textCentering">Paste your protein sequence(s) or individual KS domains in proper FASTA format:</p>
<div class="content" style="vertical-align: middle;">
<div class="textarea textCentering">
    <textarea rows="10" cols="50" name="sequenceInput" id="sequenceInput" style="color: #9b9b9b; text-align: left"
              onfocus="clearText(this)" onblur="if(this.value=='') refillText(this);">
     </textarea>
</div>
    <p class="textCentering">Or alternatively:</p>
<div class="textarea-instruction textCentering">
            <p class="textCentering">Upload protein sequences in FASTA format.
            </p>
</div> <br>


</div>
<div class="textCentering">
    <input type="file" name="fastaFile" value="Choose fasta" />
</div>
    <input class="submitButton" type="submit" value="Submit" onclick="document.getElementById('loading').style.display = 'block';" style="margin-top: 2em" />
</form>

<p class="textCentering">
    <a href="https://github.com/pcm32/PKSPredictor/wiki#about-transator">About transATor</a>
    <a href="https://github.com/pcm32/PKSPredictor/wiki#tutorial">Tutorial</a>
    <a href="https://github.com/pcm32/PKSPredictor/wiki#faqs">FAQs</a>
</p>


<script src="//ajax.googleapis.com/ajax/libs/jquery/1.9.1/jquery.min.js"></script>
<script>window.jQuery || document.write('<script src="js/vendor/jquery-1.9.1.min.js"><\/script>')</script>
<script src="js/plugins.js"></script>
<script src="js/main.js"></script>

<!-- Google Analytics: change UA-XXXXX-X to be your site's ID. -->
<script>
    var _gaq=[['_setAccount','UA-XXXXX-X'],['_trackPageview']];
    (function(d,t){var g=d.createElement(t),s=d.getElementsByTagName(t)[0];
        g.src='//www.google-analytics.com/ga.js';
        s.parentNode.insertBefore(g,s)}(document,'script'));
</script>
</body>
</html>
