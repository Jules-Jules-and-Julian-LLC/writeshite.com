import React from "react";

class LogoPage extends React.Component {
    render() {
        return (
            <div class="logo-page">
                <div class="logo">
                    <span class="logo-text noselect">Write</span>
                    <span class="logo-text noselect" role="img" id="poop-emoji" aria-label="smiling poop">
                        💩
                    </span>
                </div>
                <div class="logo-page-content">{this.props.children}</div>
            </div>
        );
    }
}

export default LogoPage;
