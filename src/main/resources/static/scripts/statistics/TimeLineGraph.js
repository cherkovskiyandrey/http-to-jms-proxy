function TimeLineGraph(idContainer) {
	this._container = document.getElementById(idContainer);
	this._dataset = new vis.DataSet();
    var options = {
        height: '200px',
        //zoomMax: 600000,
        zoomable: false,
        drawPoints: false,
        start: vis.moment().add(-120, 'seconds'), // changed so its faster
        end: vis.moment(),
        dataAxis: {
          left: {
            range: {
              min:0
            }
          }
        },
        shaded: {
          orientation: 'bottom'
        }
    };
	this._graph = new vis.Graph2d(this._container, this._dataset, options);
	this._addDataPoint();
}

TimeLineGraph.prototype.addPoint = function(point) {
    // add a new data point to the dataset
    var now = vis.moment();
    this._dataset.add({
      x: now,
      y: point
    });

    // remove all data points which are no longer visible
    var range = this._graph.getWindow();
    var interval = range.end - range.start;
    var oldIds = this._dataset.getIds({
      filter: function (item) {
        return item.x < range.start - interval;
      }
    });
    this._dataset.remove(oldIds);
}

TimeLineGraph.prototype.stop = function() {
    this._graph.destroy();
}

TimeLineGraph.prototype._addDataPoint = function() {
    var now = vis.moment();
    var rangeIn = this._graph.getWindow();
    var intervalIn = rangeIn.end - rangeIn.start;

	this._graph.setWindow(now - intervalIn, now, {animation: false});
	requestAnimationFrame(TimeLineGraph.prototype._addDataPoint.bind(this)); //TODO: bind
}