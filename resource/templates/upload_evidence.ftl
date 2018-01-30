<div title="Upload Evidence" style="display:none" id="upload-evidence">
    <p id="update-evidence-result"></p>
    <form id="upload-evidence-form" method="POST" action="/api/upload_evidence" enctype="multipart/form-data">
        <label>Description:</label>
        <textarea class="form-control" name="description" id="description"></textarea>
        <label>File:</label>
        <input class="form-control-file" name="file" id="file" type="file"/><br/>
        <input class="btn btn-primary" type="submit" id="upload-evidence-button" value="Upload"/>
    </form>
</div>