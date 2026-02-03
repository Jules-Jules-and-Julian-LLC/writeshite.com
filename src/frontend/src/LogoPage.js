import React from "react";
import { Outlet } from "react-router-dom";

class LogoPage extends React.Component {
    render() {
        return (
            <>
                <div className="logo-page">
                    <div className="logo">
                        <span className="logo-text noselect">Write</span>
                        <span className="logo-text noselect" role="img" id="poop-emoji" aria-label="smiling poop">
                        💩
                        </span>
                    </div>
                <div className="logo-page-content">
                        <Outlet />
                    </div>
                </div>
                <div className="other-creations">
                    <a href="https://ptatotp.clickclickclickclickclickclickclickclickclickclickclick.click/" target="_blank" rel="noopener noreferrer" className="ptatotp-link" title="PTATOTP">
                        <span className="ptatotp-mini"><span>P</span><span>T</span><span>A</span><span>T</span><span>O</span><span>T</span><span>P</span></span>
                    </a>
                    <a href="https://julianjocque.com/" target="_blank" rel="noopener noreferrer" className="jj-link" title="Julian Jocque's Personal Site">
                        <img src="https://julianjocque.com/assets/305529360_191387453261470_7049477229401748293_n.jpg" alt="Julian Jocque" className="jj-avatar" />
                    </a>
                </div>
            </>
        );
    }
}

export default LogoPage;