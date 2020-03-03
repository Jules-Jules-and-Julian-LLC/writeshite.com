import React from 'react';

export default class Homepage extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            username:""
        }
    }

  render() {
      return (
      <div id="homepage-content">
        <div id="logo">
            <img src="logo.svg" alt="logo"/>
        </div>
        <div>
            <input type="text" placeholder="Name" value={this.username} />
            {this.username}
        </div>
            <button type="button" onclick="alert('Hello world!')">Join Game</button>
        <div>
        </div>
        <div>
            <button type="button" onclick="alert('Hello world!')">Create Game</button>
        </div>
      </div>
      );
    }
}