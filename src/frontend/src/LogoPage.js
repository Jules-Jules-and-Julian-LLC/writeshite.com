import React from "react";

class LogoPage extends React.Component {
    render() {
        return (
            <div id="logo" class="logo-centered">
                <span class="logo-text noselect">Write</span>
                <span class="logo-text noselect" role="img" id="poop-emoji" aria-label="smiling poop">💩</span>
                {this.props.children}
            </div>
        );
    }
}

export default LogoPage;