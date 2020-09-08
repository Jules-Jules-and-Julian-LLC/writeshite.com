import React from "react";

class LogoPage extends React.Component {
    render() {
        return (
            <div id="logo" class="logo-centered noselect">
                <span class="logo-text">Write</span>
                <span class="logo-text" role="img" id="poop-emoji" aria-label="smiling poop">💩</span>
                {this.props.children}
            </div>
        );
    }
}

export default LogoPage;