<html>
<head>
    <meta charset="utf-8">
    <title>Score</title>
<#include "common_css.ftl">
</head>
<body>
<div class="container">
<#include "header.ftl">
    <div class="row">
        <h1>${evaluation.standard.name}</h1>
        <div id="tabs">
            <ul>
                <li><a href="#completion_status">Completion Status</a></li>
                <li><a href="#score_radar">Score Graph</a></li>
                <li><a href="#evaluation_details">Evaluation Details</a></li>
            </ul>
            <div id="completion_status">
                <canvas id="progress" width="400" height="400"></canvas>
            </div>
            <div id="score_radar">
                <canvas id="radar" width="600" height="600"></canvas>
            </div>
            <div id="evaluation_details">
                <h3>Overall maturity level: ${evaluation.score?string["0.##"]}</h3>
                <table class="table">
                    <tr>
                        <th>Name</th>
                        <th>Maturity Level</th>
                        <th>Severity Level</th>
                    </tr>
                <#list evaluation.chapters as chapter>
                    <tr>
                        <td>${chapter.standardNode.name}</td>
                        <td>${chapter.score?string["0.##"]}</td>
                        <td></td>
                    </tr>
                    <#list chapter.questions as question>
                        <tr>
                            <td style="padding-left:40px">${question.standardNode.name}</td>
                            <td>${question.score}</td>
                            <td>${question.severityLevel}</td>
                        </tr>
                    </#list>
                </#list>
                </table>
            </div>
        </div>
    </div>
</div>
<#include "common_js.ftl">
<script type="text/javascript">
    $("#tabs").tabs();
    var options = {
        maintainAspectRatio: true,
        responsive: false
    };
    var progress = $("#progress");
    var progressData = {
        labels: ["Completed Questions", "Incompleted Questions"],
        datasets: [
            {
                data: [${evaluation.evaluatedQuestions}, ${evaluation.totalQuestions - evaluation.evaluatedQuestions}],
                backgroundColor: ["#36A2EB", "#FF6384"],
                hoverBackgroundColor: ["#36A2EB", "#FF6384"],
            }
        ]
    }
    var progressPieChar = new Chart(progress, {
        type: 'pie',
        data: progressData,
        options: options
    });

    function ChapterScore(chapter, score) {
        this.chapter = chapter;
        this.score = score;
    }

    ChapterScore.prototype.toString = function() {
        var max_len = 15;
        if (this.chapter.length > max_len - 3) {
            return this.chapter.substr(0, max_len - 3) + "...";
        } else {
            return this.chapter;
        }
    }

    var radar = $("#radar");
    var radarData = {
        labels: [
            <#list evaluation.chapters as chapter>
                new ChapterScore("${chapter.standardNode.name}", ${chapter.score}),
            </#list>],
        datasets: [
            {
                label: "${evaluation.standard.name}",
                backgroundColor: "rgba(179,181,198,0.2)",
                borderColor: "rgba(179,181,198,1)",
                pointBackgroundColor: "rgba(179,181,198,1)",
                pointBorderColor: "#fff",
                pointHoverBackgroundColor: "#fff",
                pointHoverBorderColor: "rgba(179,181,198,1)",
                data: [
                <#list evaluation.chapters as chapter>${chapter.score},</#list>]
            },
        ]
    }
    var radarChart = new Chart(radar, {
        type: 'radar',
        data: radarData,
        options: {
            maintainAspectRatio: true,
            responsive: false,
            tooltips: {
                callbacks: {
                    title: function(tooltipItem, data) {},
                    label: function(tooltipItem, data) {
                        var ch = data.labels[tooltipItem.index];
                        return ["Chapter: " + ch.chapter,
                                "Score: " + ch.score];
                    }
                }
            },
            scale: {
                ticks: {
                    beginAtZero: true,
                    userCallback: function (numericalTick, index, ticks) {
                        return Math.round(numericalTick * 100) / 100;
                    },
                    max: ${maxScore}
                }
            }
        }
    });
</script>
</body>
</html>