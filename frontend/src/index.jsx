import React from 'react';
import {render} from 'react-dom';

class Board extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            error: null,
            isLoaded: false,
            player: null,
            status: null,
            pieces: [],
            blackCnt: null,
            whiteCnt: null,
            message: null,
        };
    }

    componentDidMount() {
        this.intervalId = setInterval(()=>{
            this.init();
        }, 1000);
    }

    componentWillUnmount(){
        clearInterval(this.intervalId);
    }

    init(){
        this.boardInit();
        this.resultInit();
    }

    boardInit() {
        fetch("/api/v1/board/")
            .then(res => res.json())
            .then(
                (result) => {
                    this.setState({
                        isLoaded: true,
                        player: result.player,
                        status: result.status,
                        pieces: JSON.parse(result.pieces)
                    });
                },
                (error) => {
                    this.setState({
                        isLoaded: true,
                        error
                    });
                }
            )
    }

    resultInit() {
        fetch("/api/v1/board/result/")
            .then(res => res.json())
            .then(
                (result) => {
                    this.setState({
                        isLoaded: true,
                        message: result.message,
                        blackCnt: result.blackCnt,
                        whiteCnt: result.whiteCnt,
                    });
                },
                (error) => {
                    this.setState({
                        isLoaded: true,
                        error
                    });
                }
            )
    }

    putClick(e) {
        const x = e.target.getAttribute('data-x');
        const y = e.target.getAttribute('data-y');
        fetch("/api/v1/board/put/",{
            method: "POST",
            headers: new Headers({
                'Content-Type': 'application/x-www-form-urlencoded', // <-- Specifying the Content-Type
            }),
            body:'x='+x+'&y='+y
        })
        .then(res => res.json())
        .then(
            (result) => {
                console.log(result);
                this.init();
            },
            (error) => {
                this.setState({
                    isLoaded: true,
                    error
                });
            }
        )
    }

    resetClick(e) {
        fetch("/api/v1/board/reset/",{
            method: "POST",
            headers: new Headers({
                'Content-Type': 'application/x-www-form-urlencoded', // <-- Specifying the Content-Type
            })
        })
        .then(res => res.json())
        .then(
            (result) => {
                console.log(result);
                this.init();
            },
            (error) => {
                this.setState({
                    isLoaded: true,
                    error
                });
            }
        )
    }

    render () {
        const { error, isLoaded, player, status, pieces, message, whiteCnt, blackCnt } = this.state;
        if (error) {
            return <div>エラーが発生しました: {error.message}</div>;
        } else if (!isLoaded) {
            return <div>Loading...</div>;
        } else {
            return(
                <>
                    <div className="main-content__board">
                        <table>
                            <tbody>
                            {(() => {
                                const items = [];
                                for (var y = 0; y < pieces.length; y++){
                                    const cell = [];
                                    for (var x = 0; x < pieces[y].length; x++){
                                        const cellContent = "";
                                        switch( pieces[x][y] ) {
                                            case 0:
                                                cellContent = <span className="circle-black"></span>;
                                                break;
                                            case 1:
                                                cellContent = <span className="circle-white"></span>;
                                                break;
                                        }
                                        cell.push(<td key={x} data-x={x} data-y={y} onClick={(e) => this.putClick(e)}>{cellContent}</td>);
                                    }
                                    items.push(<tr key={y}>{cell}</tr>);
                                }
                                return items;
                            })()}
                            </tbody>
                        </table>
                    </div>
                    <div className="main-content__menu">
                        <div className="main-content__menu__title">
                            <h1>Othello</h1>
                        </div>
                        <div className="main-content__menu__content">
                            {(() => {
                                if (status!='close') {
                                    const items = [];
                                    if(status=='pass') {
                                        items.push(<p key="message">置ける場所がないため、パスしました</p>);
                                    }
                                    items.push(<p key="player">{ player == 0 ? '黒' : '白' }の番です</p>);
                                    return items;
                                }else{
                                    if(message){
                                        return(
                                            <p key="message">{message}</p>
                                        );    
                                    }
                                }
                            })()}
                        </div>

                        <div className="main-content__menu__content">
                            <p key="blackCnt">黒 ... {blackCnt}</p>
                            <p key="whiteCnt">白 ... {whiteCnt}</p>
                        </div>

                        <a href="#" className="raised" onClick={(e) => this.resetClick(e)}>最初から<br/>始める</a>
                    </div>
                </>
            );
        }
    }
}

render(<Board/>, document.getElementById('board'));