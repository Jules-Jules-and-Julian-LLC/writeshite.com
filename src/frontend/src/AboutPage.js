import React from "react";

export default class AboutPage extends React.Component {
    render() {
        return (
            <div>
                <div>
                    <button className="button" type="button" onClick={() => window.open("/", "_self")}>
                        Return To Home Page
                    </button>
                </div>
                <br />
                This website was made by Julian Jocque. My friends and I used to play this game in person
                (with pen! and paper! like the cavemen did), but COVID made
                that impossible so I made this website so we could play remotely.
                <br /> <br />

                I looked like this at one point: <br />
                <img src="/julian.jpg" alt="Photo of my me, Julian" width="50%" height="50%"/>
                <br /> <br />

                My wife Cara and my bunny Simba looked like this at one point:<br />
                <img src="/cara_and_simba.jpg" alt="Photo of my wife Cara and my bunny Simba" width="50%" height="50%"/>
                <br /> <br />

                Cara helped a lot with workshopping ideas, both for the features and graphic design.
            </div>
        );
    }
}
