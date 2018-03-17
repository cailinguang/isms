<html>
<head>
    <meta charset="utf-8">
    <title>Evaluations</title>
<#include "common_css.ftl">
</head>
<body>
<div class="container">
<#include "header.ftl">
    <div class="table">
        <div>
            <label>Name</label>
            <input type="text" id="standard_search_string"/>
            <button id="search_button">Search</button>
        </div>

        <div id="standard_search_results"></div>
    </div>
</div>
<script id="evaluations" type="text/x-handlebars-template">
    <table class="table">
        <tr>
            <th>Name</th>
            <th>Type</th>
            <th></th>
        </tr>
        {{#each results}}
        <tr>
            <td>{{this.name}}</td>
            <td>{{this.standardType}}</td>
            <td><a href="/edit_evaluation?id={{this.standardId}}">Edit</a></td>
        </tr>
        {{/each}}
        <tr>
            <td></td>
            <td>{{#if hasPrevPage}}<a id="prevPage" href="javascript:void(0)">Prev</a>{{/if}}</td>
            <td>{{#if hasNextPage}}<a id="nextPage" href="javascript:void(0)">Next</a>{{/if}}</td>
        </tr>
    </table>
</script>
<#include "common_js.ftl">
<script type="text/javascript">
    var search_results_template = Handlebars.compile($("#evaluations").html());

    function updateSearchResults(namePattern, standardType, pageNumber) {
        IsmsRequester.requestJson(
                "/api/evaluation_library",
                "POST",
                {
                    namePattern: namePattern,
                    standardType: standardType,
                    pageNumber: pageNumber,
                    itemPerPage: 10,
                },
                function (response) {
                    if (response.pageNumber > 0) {
                        response.hasPrevPage = true;
                    }
                    $("#standard_search_results").html(search_results_template(response));
                    var prevPage = $("#prevPage");
                    if (prevPage.length && response.pageNumber > 0) {
                        prevPage.click(function () {
                            updateSearchResults(namePattern, standardType, pageNumber - 1);
                        });
                    }
                    var nextPage = $("#nextPage");
                    if (nextPage.length && response.hasNextPage) {
                        nextPage.click(function () {
                            updateSearchResults(namePattern, standardType, pageNumber + 1);
                        });
                    }
                }
        );
    }

    $("#search_button").click(function () {
        var namePattern = $("#standard_search_string").prop("value");
        updateSearchResults(namePattern, "", 0);
    });

    updateSearchResults("", "", 0);
</script>
</body>
</html>