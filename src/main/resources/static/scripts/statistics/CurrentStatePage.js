
function CurrentStatePage() {
    this._wsUrl = this.getBaseUrlAsWs() + "/stat";
    this._socket = null;
    this._timeLineInputTraffic = null;
    this._timeLineOutputTraffic = null;
    this._serverStatus = null;
    this._container = $("#current_state");
    this.stopAndHidden();
}

CurrentStatePage.prototype.getBaseUrlAsWs = function() {
    var url = "ws://" + window.location.host + window.location.pathname;
    if(url.endsWith("/")) {
        url = url.slice(0, -1)
    }
    return url;
}

CurrentStatePage.prototype.isActive = function() {
    return this._socket !== null;
}

CurrentStatePage.prototype.start = function() {
    this._initElements();
    this._initSocket();
}

CurrentStatePage.prototype.show = function() {
    this._showForm();
}

CurrentStatePage.prototype.startAndShow = function() {
    this.start();
    this.show();
}

CurrentStatePage.prototype.hidden = function() {
    this._hiddenForm();
}

CurrentStatePage.prototype.stopAndHidden = function() {
    this._hiddenForm();
    this._destroySocket();
    this._destroyElements();
}

CurrentStatePage.prototype._initElements = function() {
    this._serverStatus = new ServerStatus();
    this._timeLineInputTraffic = new TimeLineGraph("input_traffic");
    this._timeLineOutputTraffic = new TimeLineGraph("output_traffic");
}


CurrentStatePage.prototype._initSocket = function() {
    if(this._socket !== null) {
        throw new Error("CurrentStatePage is already running...");
    }
    this._socket = new WebSocket(this._wsUrl);
    this._socket.onmessage = this._updateData.bind(this);
    //TODO: на обрыв соединения и другие траблы - попап с предупреждением - попытки переподключиться
}

CurrentStatePage.prototype._updateData = function(event) {
    var data = JSON.parse(event.data);
    if(data.status !== undefined && data.status !== null) {
        this._serverStatus.update(data.status);
    }
    if(data.curInput !== undefined && data.curInput !== null) {
        this._timeLineInputTraffic.addPoint(data.curInput);
    }
    if(data.curOutput !== undefined && data.curOutput !== null) {
        this._timeLineOutputTraffic.addPoint(data.curOutput);
    }
}

CurrentStatePage.prototype._showForm = function() {
    this._container.show();
}

CurrentStatePage.prototype._destroyElements = function() {
    this._serverStatus = null;
    if(this._timeLineInputTraffic != null) {
        this._timeLineInputTraffic.stop();
        this._timeLineInputTraffic = null;
    }
    this._timeLineInputTraffic = null;
    if(this._timeLineOutputTraffic != null) {
        this._timeLineOutputTraffic.stop();
        this._timeLineOutputTraffic = null;
    }
    this._timeLineOutputTraffic = null;
}

CurrentStatePage.prototype._destroySocket = function() {
    if(this._socket !== null) {
        this._socket.onmessage = null;
        this._socket.close();
    }
    this._socket = null;
}

CurrentStatePage.prototype._hiddenForm = function() {
    this._container.hide();
}