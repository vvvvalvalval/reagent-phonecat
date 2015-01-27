function computeBallmerPeak(x) {
  // see: http://ask.metafilter.com/76859/Make-a-function-of-this-graph-Thats-like-an-antigraph
  x = x * 100;
  return (
    1-1/(1+Math.exp(-(x-6)))*.5 + Math.exp(-Math.pow(Math.abs(x-10), 2)*10)
  ) / 1.6;
}

function percentage(x) {
  return isNaN(x) ? 'N/A' : (100 - Math.round(x * 100)) + '%';
}

var BallmerPeakCalculator = React.createClass({
  getInitialState: function() {
    return {bac: 0};
  },
  handleChange: function(event) {
    this.setState({bac: event.target.value});
  },
  render: function() {
    var pct = percentage(computeBallmerPeak(this.state.bac));
    return (
      <div>
        <img src="./ballmer_peak.png" />
        <p>Credit due to <a href="http://xkcd.com/323/">xkcd</a>.</p>
        <h4>Compute your Ballmer Peak:</h4>
        <p>
          If your BAC is{' '}
          <input type="text" onChange={this.handleChange} value={this.state.bac} />
          {', '}then <b>{pct}</b> of your lines of code will be bug free.
        </p>
      </div>
    );
  }
});

React.render(
  <BallmerPeakCalculator />,
  document.getElementById('container')
);
