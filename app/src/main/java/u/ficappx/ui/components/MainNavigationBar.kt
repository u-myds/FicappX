package u.ficappx.ui.components


import u.ficappx.ui.components.enums.FragmentState
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import u.ficappx.R

@Composable
fun NavBar(searchClicked: () -> (Unit), savedClicked: () -> (Unit), settingsClicked: () -> (Unit), state: FragmentState){
    NavigationBar() {
        NavigationBarItem(
            selected = state == FragmentState.SEARCH,
            onClick = searchClicked,
            icon = { Icon(painterResource(R.drawable.search), "") }
        )

        NavigationBarItem(
            selected = state == FragmentState.SAVED,
            onClick = savedClicked,
            icon = { Icon(painterResource(R.drawable.bookmark), "") }
        )

        NavigationBarItem(
            selected = state == FragmentState.SETTINGS,
            onClick = settingsClicked,
            icon = { Icon(painterResource(R.drawable.settings), "") }
        )
    }
}