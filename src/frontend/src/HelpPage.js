import React from "react";

export default class HelpPage extends React.Component {
    render() {
        return (
            <div>
                <button className="button" type="button" onClick={() => window.open("/", "_self")}>
                    Return To Home Page
                </button>
                <div class="not-centered-text">
                    <h2>About</h2>
                    This is a game about collaboratively writing stories with your friends.
                    <br /> <br />
                    Players each write down a small part of a story, then pass it on to someone else to continue. Once a
                    story reaches its conclusion, players can click Complete Story to finish that story so it can't be added
                    to anymore.<br /> <br />

                    This continues until either time is up, or every story has been completed, then the stories are read
                    aloud by each of you. Because of this, it's recommended to have some form of voice communication to play.
                    However, if you don't read the stories aloud they will also be shown in the lobby before starting
                    the next round.

                    <br /> <br />
                    <h2>Additional Info</h2>
                    Whoever joins the lobby first is the host, and has the power to set the game settings, start and end
                    rounds, et cetera. <br />
                    This game errs on the side of freedom, so if one of your players is a troll they will be able to do
                    all sorts of annoying things. This game is really meant to be played with your personal friends on
                    some sort of a voice call.

                    <br /> <br />
                    <h2>Contact</h2>
                    You can email me at <a href="mailto:writeshitewebsite@gmail.com">writeshitewebsite@gmail.com</a>.
                    I'll probably read it and I might even reply!
                </div>
                <br />
            </div>
        );
    }
}
