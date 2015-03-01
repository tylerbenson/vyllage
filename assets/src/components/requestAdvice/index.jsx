var React = require('react');
var To = require('./FormTo');
var Subject = require('./FormSubject');
var Body = require('./FormBody');

var Form = React.createClass({
  submitHandler: function (e) {
    e.preventDefault();
  },
  render: function () {
    return (
      <div className='row'>
        <div className='twelve columns'>
          <form onSubmit={this.submitHandler}>
            <To />
            <Subject />
            <div className='message'>
              <Body />
              <div className='u-pull-right'>
                <button className="cancel-btn">cancel</button>
                <button className="send-btn">send</button>
              </div>
            </div>
          </form>
        </div>
      </div>
    );
  }
});

// var Form = React.createClass({
//   render: function () {
//     return (
//       <div className='row'>
//         <div className='twelve columns'>
//           <form>
//             <To />
//             <div className='message'>
//               <Body />
//               <div className='u-pull-right'>
//                 <button className="cancel-btn">cancel</button>
//                 <button className="send-btn">send</button>
//               </div>
//             </div>
//           </form>
//         </div>
//       </div>
//     );
//   }
// });

module.exports = Form;