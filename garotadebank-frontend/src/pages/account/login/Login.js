import './Login.css';
import React from 'react';
import Button from '@material-ui/core/Button';
import TextField from "@material-ui/core/TextField";
import {withRouter} from "react-router-dom";
import Container from "@material-ui/core/Container";
import Garotade from "../../../components/Garotade";


function Login({history}) {

    let account = '';

    function onChange(evt) {
        account = evt.target.value;
    }

    function onClick() {
        history.push(`/account/${account}`);
    }

    return(
        <Container className="Login">
            <Garotade />
            <form>
                <TextField label="Digite o número da sua conta" variant="outlined" color="primary" onChange={onChange} />
                <Button variant="outlined" color="primary" onClick={onClick} type='button'>
                    Acessar conta
                </Button>
                <Button variant="outlined" color="primary" href="/new-account" type='button'>
                    Criar nova conta
                </Button>
            </form>
        </Container>
    );
}


export default withRouter(Login);
