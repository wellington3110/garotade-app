import React from 'react';
import TextField from "@material-ui/core/TextField";
import Button from "@material-ui/core/Button";
import { withRouter } from "react-router-dom";
import './NewAccount.css';
import Container from "@material-ui/core/Container";
import env from "../../../config/env";
import Garotade from "../../../components/Garotade";

function NewAccount({ accountAPI, history }) {

    let client = '';
    let cpf = '';

    const onChangeClientName = evt => client = evt.target.value;
    const onChangeCpf = evt => cpf = evt.target.value;


    async function onClickCreateAccount() {
        try {
            const accountNumber = await accountAPI.create({ client, cpf });
            history.push(`/account/${accountNumber}`);
        } catch (e) {
            alert('ocorreu um erro ao criar uma nova conta');
        }
    }

    return (
        <Container className="NewAccount">
            <Garotade />
            <form>
                <TextField
                    onChange={onChangeClientName}
                    label="Digite seu nome completo"
                    variant="outlined"
                    color="primary"/>
                {env.CPF_FLOW && <TextField
                    onChange={onChangeCpf}
                    label="Digite seu cpf"
                    variant="outlined"
                    color="primary"/> }
                <Button variant="outlined" color="primary" onClick={onClickCreateAccount}>
                    Criar nova conta
                </Button>
            </form>
        </Container>
    );
}

export default withRouter(NewAccount)
