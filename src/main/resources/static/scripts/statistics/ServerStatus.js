function ServerStatus() {
    this._success = $("#server_status .success td")[1];
    this._process = $("#server_status .warning td")[1];
    this._error = $("#server_status .danger td")[1];
}

ServerStatus.prototype.update = function(val) {
    if(val.success !== undefined && val.success !== null) {
        this._success.innerHTML = val.success;
    }
    if(val.process !== undefined && val.process !== null) {
        this._process.innerHTML = val.process;
    }
    if(val.error !== undefined && val.error !== null) {
        this._error.innerHTML = val.error;
    }
}