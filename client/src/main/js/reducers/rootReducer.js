const initialState = {
    text : 'initial text'
}

//https://redux.js.org/recipes/structuring-reducers/using-combinereducers

const rootReducer = (state=initialState, action) => {
    return { text: state.text + "x"};
    }

export default rootReducer;