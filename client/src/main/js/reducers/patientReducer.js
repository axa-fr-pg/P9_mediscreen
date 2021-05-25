const patientState = {
    text: 'patient '
};

const patientReducer = (state = patientState, action) => {
    console.log("patientReducer with action " + action.type + " " + action.payload);
    return {text: state.text + "p"};
};

export default patientReducer;